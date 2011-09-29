/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public final class GConcurrent {

   public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;


   private GConcurrent() {
   }

   public static int              AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

   private static ExecutorService DEFAULT_EXECUTOR;


   //   private final static GLogger   LOGGER               = GLogger.instance();


   //   static {
   //      LOGGER.info("GConcurrent: Available Processors: " + AVAILABLE_PROCESSORS);
   //   }


   public static ExecutorService createExecutor(final int maximumPoolSize) {
      return createExecutor(maximumPoolSize, DEFAULT_THREAD_PRIORITY);
   }


   public static ExecutorService createExecutor(final int maximumPoolSize,
                                                final int threadPriority) {
      final ThreadPoolExecutor result = new ThreadPoolExecutor(0, maximumPoolSize, 10, TimeUnit.SECONDS,
               new SynchronousQueue<Runnable>(), defaultThreadFactory(threadPriority));
      result.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
      return result;
   }


   private static ThreadFactory defaultThreadFactory(final int threadPriority) {
      return new DefaultThreadFactory(threadPriority);
   }

   private static class DefaultThreadFactory
            implements
               ThreadFactory {
      private static final AtomicInteger poolNumber    = new AtomicInteger(1);

      private final ThreadGroup          _group;
      private final AtomicInteger        _threadNumber = new AtomicInteger(1);
      private final String               _namePrefix;
      private final int                  _threadPriority;


      DefaultThreadFactory(final int threadPriority) {
         final SecurityManager s = System.getSecurityManager();
         _group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         _namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
         _threadPriority = threadPriority;
      }


      @Override
      public Thread newThread(final Runnable runnable) {
         final Thread t = new Thread(_group, runnable, _namePrefix + _threadNumber.getAndIncrement(), 0);
         //         if (t.isDaemon()) {
         t.setDaemon(false);
         //         }
         //         if (t.getPriority() != _threadPriority) {
         t.setPriority(_threadPriority);
         //         }
         return t;
      }
   }


   public synchronized static ExecutorService getDefaultExecutor() {
      if (DEFAULT_EXECUTOR == null) {
         final int maximumPoolSize = AVAILABLE_PROCESSORS * 32;
         //final int maximumPoolSize = AVAILABLE_PROCESSORS * 16;
         //         LOGGER.info("GConcurrent: Initializing default executor, maximum " + maximumPoolSize + " threads");
         DEFAULT_EXECUTOR = createExecutor(maximumPoolSize, DEFAULT_THREAD_PRIORITY);
      }

      return DEFAULT_EXECUTOR;
   }


   public static void awaitTermination(final ExecutorService executor) {
      try {
         executor.awaitTermination(100, TimeUnit.DAYS);
      }
      catch (final InterruptedException e) {
         throw new RuntimeException(e);
      }
   }


   public static <T> List<T> resolve(final List<Future<T>> futures) {
      try {
         final ArrayList<T> result = new ArrayList<T>(futures.size());
         for (final Future<T> future : futures) {
            result.add(future.get());
         }
         return result;
      }
      catch (final InterruptedException e) {
         throw new RuntimeException(e);
      }
      catch (final ExecutionException e) {
         throw new RuntimeException(e);
      }
   }

}
