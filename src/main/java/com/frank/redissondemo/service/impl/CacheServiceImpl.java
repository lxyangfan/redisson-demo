package com.frank.redissondemo.service.impl;

import com.frank.redissondemo.service.CacheService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RObject;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

  private final RedissonClient redissonClient;

  @Override
  public <T> T get(String key) {
    RBucket<T> bucket = redissonClient.getBucket(key);
    return bucket.get();
  }

  @Override
  public void set(String key, Object value, long expireTime) {
    RBucket<Object> bucket = redissonClient.getBucket(key);
    bucket.set(value, Duration.ofMillis(expireTime));
  }

  @Override
  public void setMapItem(String key, String mapKey, Object value, long expireTime) {
    RMap<String, Object> map = redissonClient.getMap(key);
    map.put(mapKey, value);
    map.expire(expireTime, TimeUnit.MILLISECONDS);
  }

  @Override
  public <T> T getMapItem(Class<T> clazz, String key, String mapKey) {
    RMap<String, T> map = redissonClient.getMap(key);
    return map.get(mapKey);
  }

  @Override
  public <T> void batchSetMap(String key, Map<String, T> valueMap, long expireTime) {

    RMap<String, T> map = redissonClient.getMap(key);
    map.putAll(valueMap);
    map.expire(expireTime, TimeUnit.MILLISECONDS);
  }

  @Override
  public <T> List<T> batchGetMap(Class<T> clazz, String cacheKey, Set<String> keys) {

    RMap<String, T> map = redissonClient.getMap(cacheKey);
    Collection<T> values = map.getAll(keys).values();
    return new ArrayList<>(values);
  }

  @Override
  public <T> List<T> batchGetMapItems(Class<T> clazz, String cacheKey) {
    //    RObject.is
    RMap<String, T> map = redissonClient.getMap(cacheKey);
    if (map.isExists()) {
      return new ArrayList<>(map.readAllValues());
    } else {
      return null;
    }
  }

  @Override
  public void deleteMap(String key) {
    RMap<String, Object> map = redissonClient.getMap(key);
    map.delete();
  }

  @Override
  public void deleteMapItem(String key, String mapKey) {
    RMap<String, Object> map = redissonClient.getMap(key);
    map.remove(mapKey);
  }
}
