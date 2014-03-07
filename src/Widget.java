import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Widget
{
	static volatile private int cost;
	static volatile private int quantity;
	Lock myLock = new ReentrantLock();
	
	public Widget(int c, int q)
	{
		cost = c;
		quantity = q;
	}
	
	public int[] getState()
	{
		while(!myLock.tryLock());
		int[] result = {cost, quantity};
		return result;
	}
	
	public void  update(int c, int q)
	{
		cost += c;
		quantity += q;
		myLock.unlock();
	}
	
	public void releaseLock()
	{
		myLock.unlock();
	}
}
