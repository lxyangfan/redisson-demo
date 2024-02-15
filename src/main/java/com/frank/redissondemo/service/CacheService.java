package com.frank.redissondemo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheService {

  <T> T get(String key);

  void set(String key, Object value, long expireTime);

  void setMapItem(String key, String mapKey, Object value, long expireTime);

  <T> T getMapItem(Class<T> clazz, String key, String mapKey);

  <T> void batchSetMap(String key, Map<String, T> valueMap, long expireTime);

  <T> List<T> batchGetMap(Class<T> clazz, String cacheKey, Set<String> keys);

  <T> List<T> batchGetMapItems(Class<T> clazz, String cacheKey);


  /**
   * 删除缓存, 将key对应的缓存删除
   * @param key
   */
  void deleteMap(String key);

  void deleteMapItem(String key, String mapKey);

  void batchDeleteMapItems(String key, Set<String> mapKeys);
}
