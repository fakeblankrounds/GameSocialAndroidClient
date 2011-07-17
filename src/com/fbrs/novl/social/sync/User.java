package com.fbrs.novl.social.sync;

public class User {

	private String Username;

	private String Online;

	private String Status;

	private String Game;

	private String Record;
	
	private boolean deleted;
	
	private int mUserID;
	
	public int getUserId() {
        return mUserID;
    }

    public String getUserName() {
        return Username;
    }
    
    public String getOnline()
    {
    	return Online;
    }
    
    public String getStatus()
    {
    	return Status;
    }
    
    public String getGame()
    {
    	return Game;
    }

    public boolean isDeleted() {
        return deleted;
    }


	public User(String username, String online, String stat, String game)
	{
		Username = username;
		Online = online;
		Status = stat;
		Game = game;
	}

	public static User valueOf(String u)
	{
		//check the validity of the request
		if(u != "") {
			String[] split = u.split("-");
			if(split.length != 3)
				return null;

			String online, stat;
			if(split[1].charAt(0) == 'y')
				online = "Online";
			else
				online = "Offline";
			if(split[1].charAt(1) == 'i')
				stat = "In game";
			else if(split[1].charAt(1) == 'b')
				stat = "Busy";
			else if(split[1].charAt(1) == 'o')
				stat = "Chillin";
			else
				stat = "Not Available";
			return new User(split[0],online, stat, split[2]);
		}
		else
			return null;
	}

}
