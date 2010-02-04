/**
 * Allows objects to receive notifications when memory is low.
 */

package memory;

import java.lang.management.*;
import java.util.*;
import javax.management.*;

public class LowMemoryMonitor {
	private List<LowMemoryListener> listeners = new ArrayList<LowMemoryListener>();
	private static LowMemoryMonitor instance = new LowMemoryMonitor();

	/**
	 * Implement singleton pattern, since it doesn't make sense to have
	 * more than one of these objects hanging around.
	 */
	public static LowMemoryMonitor getInstance() {
		return instance;
	}

	private LowMemoryMonitor() {
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		NotificationEmitter emitter = (NotificationEmitter) mbean;
		emitter.addNotificationListener(new NotificationListener() {
			public void handleNotification(Notification notif, Object handback) {
				if(notif.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
					for(LowMemoryListener listener : listeners) {
						listener.memoryLow();
					}
				}
			}
		}, null, null);
	}

	/**
	 * Available memory is heap type and can have its threshold set.
	 */
	private static MemoryPoolMXBean getHeapMemoryPool() {
		for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
			if (pool.getType() == MemoryType.HEAP && pool.isUsageThresholdSupported()) {
				return pool;
			}
		}
		// This should not happen!
		throw new AssertionError("Could not find memory pool!");
	}

	/**
	 * Sets the low memory notification based on the percentage of memory left.
	 */
	public void setMemoryThreshold(double percentage) {
		MemoryPoolMXBean pool = getHeapMemoryPool();
		long maxMemory = pool.getUsage().getMax();
		pool.setUsageThreshold((long) ((1.0 - percentage) * maxMemory));
	}

	/**
	 * Sets the low memory notification based on the amount of memory left.
	 */
	public void setMemoryThreshold(long size) {
		MemoryPoolMXBean pool = getHeapMemoryPool();
		long maxMemory = pool.getUsage().getMax();
		pool.setUsageThreshold(maxMemory - size);
	}

	/**
	 * Returns wether or not the memory threshold has been exceeded.
	 */
	public boolean isMemoryLow() {
		MemoryPoolMXBean pool = getHeapMemoryPool();
		return pool.isUsageThresholdExceeded();
	}

	public void addListener(LowMemoryListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LowMemoryListener listener) {
		listeners.remove(listener);
	}
}
