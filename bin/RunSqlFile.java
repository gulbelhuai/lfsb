import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunSqlFile {
    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            throw new IllegalArgumentException("Usage: RunSqlFile <jdbcUrl> <user> <password> <sqlFile> <encoding>");
        }

        String jdbcUrl = args[0];
        String user = args[1];
        String password = args[2];
        Path sqlFile = Path.of(args[3]);
        String encoding = args[4];

        String content = Files.readString(sqlFile, Charset.forName(encoding));
        List<String> statements = splitStatements(content);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            int index = 0;
            for (String sql : statements) {
                index++;
                if (sql.isBlank()) {
                    continue;
                }
                System.out.println(">>> [" + index + "/" + statements.size() + "] " + preview(sql));
                executeSql(connection, statement, sql);
            }
            connection.commit();
        }
        System.out.println("SQL execution completed.");
    }

    private static void executeSql(Connection connection, Statement statement, String sql) throws Exception {
        String normalized = sql.replaceAll("\\s+", " ").trim();
        if (normalized.toUpperCase(Locale.ROOT).startsWith("ALTER TABLE")
                && normalized.toUpperCase(Locale.ROOT).contains("ADD COLUMN IF NOT EXISTS")) {
            executeAlterAddColumnIfMissing(connection, statement, sql);
            return;
        }
        statement.execute(sql);
    }

    private static void executeAlterAddColumnIfMissing(Connection connection, Statement statement, String sql) throws Exception {
        Matcher matcher = Pattern.compile("(?is)^ALTER\\s+TABLE\\s+`?([a-zA-Z0-9_]+)`?\\s+(.*)$").matcher(sql.trim());
        if (!matcher.find()) {
            statement.execute(sql);
            return;
        }
        String tableName = matcher.group(1);
        String definitionsPart = matcher.group(2).trim();
        List<String> definitions = splitTopLevel(definitionsPart);
        for (String definition : definitions) {
            String trimmed = definition.trim();
            Matcher addColumnMatcher = Pattern.compile("(?is)^ADD\\s+COLUMN\\s+IF\\s+NOT\\s+EXISTS\\s+`?([a-zA-Z0-9_]+)`?\\s+(.*)$")
                    .matcher(trimmed);
            if (!addColumnMatcher.find()) {
                String alterSql = "ALTER TABLE `" + tableName + "` " + trimmed;
                statement.execute(alterSql);
                continue;
            }
            String columnName = addColumnMatcher.group(1);
            String columnDefinition = addColumnMatcher.group(2).trim();
            if (columnExists(connection, tableName, columnName)) {
                System.out.println("    skip existing column: " + tableName + "." + columnName);
                continue;
            }
            String alterSql = "ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + columnDefinition;
            statement.execute(alterSql);
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws Exception {
        String sql = "SELECT COUNT(1) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = '"
                + tableName + "' AND column_name = '" + columnName + "'";
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    private static List<String> splitTopLevel(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int parentheses = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == '(' && !inSingleQuote && !inDoubleQuote) {
                parentheses++;
            } else if (c == ')' && !inSingleQuote && !inDoubleQuote && parentheses > 0) {
                parentheses--;
            }

            if (c == ',' && !inSingleQuote && !inDoubleQuote && parentheses == 0) {
                parts.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        String trailing = current.toString();
        if (!trailing.isBlank()) {
            parts.add(trailing);
        }
        return parts;
    }

    private static List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
                continue;
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote) {
                if (c == '-' && next == '-') {
                    inLineComment = true;
                    i++;
                    continue;
                }
                if (c == '/' && next == '*') {
                    inBlockComment = true;
                    i++;
                    continue;
                }
            }

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }

            if (c == ';' && !inSingleQuote && !inDoubleQuote) {
                String statement = current.toString().trim();
                if (!statement.isBlank()) {
                    statements.add(statement);
                }
                current.setLength(0);
                continue;
            }

            current.append(c);
        }

        String trailing = current.toString().trim();
        if (!trailing.isBlank()) {
            statements.add(trailing);
        }
        return statements;
    }

    private static String preview(String sql) {
        String normalized = sql.replaceAll("\\s+", " ").trim();
        return normalized.length() > 120 ? normalized.substring(0, 120) + "..." : normalized;
    }
}
