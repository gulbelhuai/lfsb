package com.ruoyi.shebao.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@Slf4j
public class CommonService {

  // 使用ConcurrentHashMap来模拟分布式锁存储
  private final ConcurrentHashMap<String, Long> lockMap = new ConcurrentHashMap<>();

  public void lockDo(String lockKey, Runnable function, long waitTime, long leaseTime, TimeUnit unit) {
    long waitTimeMillis = unit.toMillis(waitTime);
    long leaseTimeMillis = unit.toMillis(leaseTime);
    long startTime = System.currentTimeMillis();
    boolean lockAcquired = false;

    try {
      // 尝试获取锁
      lockAcquired = tryAcquireLock(lockKey, leaseTimeMillis);
      
      // 如果未获取到锁，等待重试
      while (!lockAcquired && (System.currentTimeMillis() - startTime) < waitTimeMillis) {
        Thread.sleep(100); // 等待100ms后重试
        lockAcquired = tryAcquireLock(lockKey, leaseTimeMillis);
      }

      log.info("加锁: key={}, result={}, wait={}, lease={}, unit={}", lockKey, lockAcquired, waitTime, leaseTime, unit);
      
      if (!lockAcquired) {
        log.error("加锁失败: key={}, wait={}, lease={}, unit={}", lockKey, waitTime, leaseTime, unit);
        throw new IllegalArgumentException("获取锁状态失败，请稍后再试");
      }

      function.run(); // 调用无参数无返回值的方法

    } catch (InterruptedException e) {
      log.error("加锁中断: key={}, wait={}, lease={}, unit={}", lockKey, waitTime, leaseTime, unit, e);
      Thread.currentThread().interrupt();
      throw new IllegalArgumentException("获取锁状态失败，请稍后再试.");
    } finally {
      if (lockAcquired) {
        releaseLock(lockKey);
      }
    }
  }

  public <T> T lockDo(String lockKey, Supplier<T> function, long waitTime, long leaseTime, TimeUnit unit) {
    long waitTimeMillis = unit.toMillis(waitTime);
    long leaseTimeMillis = unit.toMillis(leaseTime);
    long startTime = System.currentTimeMillis();
    boolean lockAcquired = false;

    try {
      // 尝试获取锁
      lockAcquired = tryAcquireLock(lockKey, leaseTimeMillis);
      
      // 如果未获取到锁，等待重试
      while (!lockAcquired && (System.currentTimeMillis() - startTime) < waitTimeMillis) {
        Thread.sleep(100); // 等待100ms后重试
        lockAcquired = tryAcquireLock(lockKey, leaseTimeMillis);
      }

      log.info("加锁: key={}, result={}, wait={}, lease={}, unit={}", lockKey, lockAcquired, waitTime, leaseTime, unit);
      
      if (!lockAcquired) {
        log.error("加锁失败: key={}, wait={}, lease={}, unit={}", lockKey, waitTime, leaseTime, unit);
        throw new IllegalArgumentException("获取锁状态失败，请稍后再试");
      }

      return function.get(); // 调用有返回值的方法

    } catch (InterruptedException e) {
      log.error("加锁中断: key={}, wait={}, lease={}, unit={}", lockKey, waitTime, leaseTime, unit, e);
      Thread.currentThread().interrupt();
      throw new IllegalArgumentException("获取锁状态失败，请稍后再试.");
    } finally {
      if (lockAcquired) {
        releaseLock(lockKey);
      }
    }
  }

  /**
   * 尝试获取锁
   */
  private boolean tryAcquireLock(String lockKey, long leaseTimeMillis) {
    long expireTime = System.currentTimeMillis() + leaseTimeMillis;
    
    // 清理过期的锁
    cleanupExpiredLocks();
    
    // 尝试获取锁
    Long existingExpireTime = lockMap.get(lockKey);
    
    if (existingExpireTime == null) {
      // 没有锁，尝试创建新锁
      Long previousExpireTime = lockMap.putIfAbsent(lockKey, expireTime);
      return previousExpireTime == null; // 如果返回null说明成功获取锁
    } else if (existingExpireTime < System.currentTimeMillis()) {
      // 锁已过期，尝试替换
      boolean replaced = lockMap.replace(lockKey, existingExpireTime, expireTime);
      return replaced;
    }
    
    return false; // 锁被其他线程持有且未过期
  }

  /**
   * 释放锁
   */
  private void releaseLock(String lockKey) {
    lockMap.remove(lockKey);
    log.info("释放锁: key={}", lockKey);
  }

  /**
   * 清理过期的锁
   */
  private void cleanupExpiredLocks() {
    long currentTime = System.currentTimeMillis();
    lockMap.entrySet().removeIf(entry -> entry.getValue() < currentTime);
  }

  public void lockDo(String lockKey, Runnable function) {
    this.lockDo(lockKey, function, 3, 30, TimeUnit.SECONDS);
  }

  public <T> T lockDo(String lockKey, Supplier<T> function) {
    return this.lockDo(lockKey, function, 3, 30, TimeUnit.SECONDS);
  }
}
