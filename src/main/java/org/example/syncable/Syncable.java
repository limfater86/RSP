package org.example.syncable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dperminov
 * @since 20.04.2024
 */
public class Syncable {
    public static Syncable S = new Syncable() {
    };

    private static final long TIMEOUT = 20000;

    /**
     * Синхронный вызов с блокировкой на чтение по стандартному таймауту
     *
     * @param lock     блокировщик
     * @param runnable исполняемый код
     */
    public void syncReadAndRun(ReentrantReadWriteLock lock, Runnable runnable) {
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new RuntimeException("Occurred in the thread " + Thread.currentThread().toString());
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Синхронный вызов с блокировкой по стандартному таймауту
     *
     * @param lock     блокировщик
     * @param runnable исполняемый код
     */
    public void syncWriteAndRun(ReentrantReadWriteLock lock, Runnable runnable) {
        boolean locked = false;
        try {
            if (locked = lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new RuntimeException("Occurred in the thread " + Thread.currentThread().toString());
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if (locked) lock.writeLock().unlock();
        }
    }
}
