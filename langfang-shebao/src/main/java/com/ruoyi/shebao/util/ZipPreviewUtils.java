package com.ruoyi.shebao.util;

import com.ruoyi.common.constant.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZIP 解压与图片预览工具
 */
public final class ZipPreviewUtils
{
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private ZipPreviewUtils()
    {
    }

    public static List<String> extractImages(Path zipFile, Path targetDir, Path profileDir) throws IOException
    {
        Files.createDirectories(targetDir);
        Set<String> relativePathSet = new LinkedHashSet<>();
        try (InputStream inputStream = Files.newInputStream(zipFile);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream))
        {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null)
            {
                if (entry.isDirectory())
                {
                    continue;
                }
                String entryName = entry.getName().replace("\\", "/");
                if (isIgnoredZipEntry(entryName))
                {
                    continue;
                }
                String fileName = Path.of(entryName).getFileName().toString();
                String extension = extensionOf(fileName);
                if (!IMAGE_EXTENSIONS.contains(extension))
                {
                    continue;
                }
                Path resolved = targetDir.resolve(entryName).normalize();
                if (!resolved.startsWith(targetDir.normalize()))
                {
                    throw new IOException("ZIP 文件包含非法路径");
                }
                Files.createDirectories(resolved.getParent());
                Files.copy(zipInputStream, resolved, StandardCopyOption.REPLACE_EXISTING);
                relativePathSet.add(toResourcePath(profileDir, resolved));
            }
        }
        return new ArrayList<>(relativePathSet);
    }

    public static void deleteDirectory(Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            return;
        }
        try (var stream = Files.walk(path))
        {
            stream.sorted((a, b) -> b.compareTo(a)).forEach(item ->
            {
                try
                {
                    Files.deleteIfExists(item);
                }
                catch (IOException ignored)
                {
                }
            });
        }
    }

    private static String extensionOf(String fileName)
    {
        int index = fileName.lastIndexOf('.');
        if (index < 0)
        {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    private static String toResourcePath(Path profileDir, Path absolutePath)
    {
        String relative = profileDir.normalize().relativize(absolutePath.normalize()).toString().replace("\\", "/");
        return Constants.RESOURCE_PREFIX + "/" + relative;
    }

    private static boolean isIgnoredZipEntry(String entryName)
    {
        if (entryName == null || entryName.isBlank())
        {
            return true;
        }
        String normalized = entryName.replace("\\", "/");
        if (normalized.startsWith("__MACOSX/"))
        {
            return true;
        }
        String fileName = Path.of(normalized).getFileName().toString();
        return fileName.startsWith("._") || ".DS_Store".equalsIgnoreCase(fileName);
    }
}
