export const getFileUrl = (path) => {
  if (!path) return null;
  // 如果已经是完整 URL（http://, https://, //），直接返回
  if (path.startsWith('http') || path.startsWith('//')) return path;
  // 否则添加后端文件接口前缀
  return `/api/files?path=${encodeURIComponent(path)}`;
};
