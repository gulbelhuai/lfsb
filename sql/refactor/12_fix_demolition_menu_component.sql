-- 修正“拆迁居民登记”菜单组件，避免误跳到“被征地居民”页面
UPDATE sys_menu
SET component = 'shebao/demolitionResident/index'
WHERE path = 'demolition'
  AND perms = 'shebao:person:demolition:list';
