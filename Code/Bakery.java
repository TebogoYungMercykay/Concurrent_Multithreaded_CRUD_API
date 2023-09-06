import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Bakery implements Lock {
	private volatile boolean[]  flag;
	private volatile int[] label;
	private int numThreads;

	public Bakery (int num) {
		this.numThreads = num;
		this.flag = new boolean[this.numThreads];
		this.label = new int[this.numThreads];
		for (int i = 0; i < this.numThreads; i++) {
			this.flag[i] = false;
			this.label[i] = 0;
		}
	}

	@Override
	public void lock() {
		int index = this.filterThread(String.valueOf(Thread.currentThread().getName()));
		this.flag[index] = true;
		this.label[index] = max_label(label);
		for (int k = 0; k < this.numThreads; k++) {
			// while((k!=i) && (flag[k] && label[k]<label[i]))
			while (((this.flag[k] == true) && (this.label[k] < this.label[index])) || ((this.label[k] == this.label[index]) && (k < index))) {
				// spin wait
			}
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	public boolean tryLock() {
		throw new UnsupportedOperationException();
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unlock() {
		this.flag[this.filterThread(String.valueOf(Thread.currentThread().getName()))] = false;
	}

	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}

	public int filterThread(String thread) {
		return Integer.parseInt(thread.substring(thread.length() - 1));
	}

	public int max_label(int[] labels_arr) {
		int maxValue = labels_arr[0];
		for (int i = 1; i < labels_arr.length; i++) {
			if (labels_arr[i] > maxValue) {
				maxValue = labels_arr[i];
			}
		}
		return maxValue + 1;
	}
}