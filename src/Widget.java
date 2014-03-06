public class Widget
{
	int cost;
	int quantity;
	
	public Widget(int c, int q)
	{
		cost = c;
		quantity = q;
	}
	
	public void update(int c, int q)
	{
		cost += c;
		quantity += q;
	}
}
