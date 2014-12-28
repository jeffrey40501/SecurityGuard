package com.beiluoshimen.securityguard.market;

import java.math.BigInteger;
import java.util.List;


/**
 * A simple object to represent an account and its URL for viewing.
 * 
 * @author Hsieh Yu-Hua
 * @date Dec 23, 201410:12:18 PM
 */

//Identifies a domain object to be persisted to MongoDB.
public class Account {
	private BigInteger id;
	private String username;
	private String password;
	private int coin;
	//this list should contains characters that one has...
	private List<Integer> characters;
	
	
	//Easily BUGGGGGG!!!!!!
//	JsonMappingException: No suitable constructor found for type [simple type, class ]:
//	can not instantiate from JSON object
	//How to solve ?
	/*
Sol:http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
So, finally I realized what the problem is. It is not a Jackson configuration issue as I doubted.
Actually the problem was in ApplesDO Class:
public class ApplesDO {
    private String apple;
    public String getApple() {
        return apple;
    }
    public void setApple(String apple) {
        this.apple = apple;
    }
    public ApplesDO(CustomType custom) {
        //constructor Code
    }
}
There was a custom constructor defined for the class making it the default constructor. 
Introducing a dummy constructor has made the error to go away:

public class ApplesDO {
    private String apple;
    public String getApple() {
        return apple;
    }
    public void setApple(String apple) {
        this.apple = apple;
    }
    public ApplesDO(CustomType custom) {
        //constructor Code
    }
    //Introducing the dummy constructor
    public ApplesDO() {
    }
}*/
	//SO WE MUST ADD Account()!!!!!!!!!!Fu*k this bug...
	public Account(){

	}
	public Account(String username,String password, int coin, List<Integer> characters) {
		super();
		this.username = username;
		this.password = password;
		this.coin = coin;
		this.characters = characters;
	}

	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public int getCoin() {
		return coin;
	}


	public void setCoin(int coin) {
		this.coin = coin;
	}


	public List<Integer> getCharacters() {
		return characters;
	}


	public void setCharacters(List<Integer> characters) {
		this.characters = characters;
	}
	
}
