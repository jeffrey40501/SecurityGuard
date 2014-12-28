package com.beiluoshimen.securityguard.market;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

public class AccountSvcClientApiTest {
	private final String TEST_URL = "https://localhost:8443";

	private AccountSvcApi accountService = new RestAdapter.Builder()
	.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
	.create(AccountSvcApi.class);
	
//	private AccountSvcApi accountService = new RestAdapter.Builder()
//			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
//			.create(AccountSvcApi.class);
	
//	@Test
	public void testAccountAdd()throws Exception {
		
		//NEED TO LOGIN FIRST!!!
		accountService.login("coursera", "changeit");
		
		
		
		//TEST addAccount
	 List<Integer> characters = new ArrayList<Integer>();
	 characters.add(122);
	 characters.add(224);
	 characters.add(344);
//	 Account aa = new Account("Andy2", "123",222, characters);
//	 assertTrue(accountService.addAccount(new Account("Andy1", "123",222, characters)));
//	 
	 
	 
		//Test findByUsernameAndPassword ok
//	 Collection<Account> accounts = accountService.findByUsernameAndPassword("Andy1", "123");
//	 assertTrue(accounts.contains(new Account("Andy1", "123", 222, characters)));	 
	 
	 
	 	//TEst add coin ok 
//	 assertTrue(accountService.addCoin("Andy1", "123", 222));
	 
	 //Test  getCharacters
	 ArrayList<Character> list = (ArrayList<Character>) accountService.getCharacters();
	 for (Iterator<Character> iterator = list.iterator(); iterator.hasNext();) {
		Character a = (Character) iterator.next();
		System.out.println("No:"+a.getNo()+"price:"+a.getPrice()+"pic:"+a.getPic());

	}
	 
	 //buy character ok
//	 assertTrue(accountService.buyCharacter("Andy1", "123", 103));
	 
	 

	 
	 
	 
	}

}
