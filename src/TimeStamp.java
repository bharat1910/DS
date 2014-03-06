class TimeStamp {
	Integer lamport;
	int[] vector;
	int nodeIndex;
	
	public TimeStamp(int size, int nodeIndex)
	{
		lamport = 0;
		vector = new int[size];
		this.nodeIndex = nodeIndex;
	}
	
	public String getVector()
	{
		String s = "";
		for (int a : vector) {
			s += a + ",";
		}
		
		return s.substring(0, s.length() - 1);
	}
	
	public int getLamport()
	{
		return lamport;
	}
	
	public void increment(int l, int[] v)
	{
		if (l == -1) {
			lamport += 1;
		} else {
			lamport = Math.max(l, lamport) + 1;
		}
		
		
		if (v == null) {
			vector[nodeIndex]++;
			return;
		} else {
			for (int i=0; i<v.length; i++) {
				if (i != nodeIndex) {
					vector[i] = Math.max(v[i], vector[i]);
				} else {
					vector[i] = vector[i] + 1;
				}
			}
		}
	}
}