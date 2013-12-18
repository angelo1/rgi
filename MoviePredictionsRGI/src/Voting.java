
public class Voting {
	
	User user;
	Item item;
	int rating;
	
	
	public Voting(User u, Item i, int rat)
	{
		
		user = u;
		item = i;
		rating = rat;
		
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public Item getItem() {
		return item;
	}


	public void setItem(Item item) {
		this.item = item;
	}


	public int getRating() {
		return rating;
	}


	public void setRating(int rating) {
		this.rating = rating;
	}

}
