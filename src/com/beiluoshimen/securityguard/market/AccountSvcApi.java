package com.beiluoshimen.securityguard.market;


import java.util.Collection;


import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a AccountSvc.(clientSide class) 
 * The interface is used to provide a contract for client/server interactions.
 * This class is annotated with "Retrofit" annotations ,
 * so that clients can automatically convert the api into implementation.
 * 
 * @author Hsieh Yu-Hua
 * @date Dec 23, 201410:22:09 PM
 */
public interface AccountSvcApi {
	public static final String PASSWORD_PARAMETER = "password";
	public static final String USERNAME_PARAMETER = "username";
	public static final String COIN_PARAMETER = "coin";
	public static final String CHARACTER_PARAMETER = "character";
	
	public static final String LOGIN_PATH = "/login";
	
	public static final String LOGOUT_PATH = "/logout";
	
	// The path where we expect the AccountSvc to live
	public static final String ACCOUNT_SVC_PATH = "/account";
	
	public static final String COIN_PATH = ACCOUNT_SVC_PATH + "/coin";
	public static final String CHARACTER_PATH = ACCOUNT_SVC_PATH + "/character";
	public static final String BUY_PATH = ACCOUNT_SVC_PATH + "/buy";

	//Denotes that the request body will use form URL encoding.
	//Fields should be declared as parameters and annotated with @Field.
	@FormUrlEncoded
	@POST(LOGIN_PATH)
	public Void login(@Field(USERNAME_PARAMETER) String username, 
					@Field(PASSWORD_PARAMETER) String password);
	
	@GET(LOGOUT_PATH)
	public Void logout();

	@GET(ACCOUNT_SVC_PATH)
	public Collection<Account> findByUsernameAndPassword(@Query(USERNAME_PARAMETER) String username,
														@Query(PASSWORD_PARAMETER) String password);
	
	@POST(ACCOUNT_SVC_PATH)
	public boolean addAccount(@Body Account v);
	
	@POST(COIN_PATH)
	public boolean addCoin(@Query(USERNAME_PARAMETER) String username,
						@Query(PASSWORD_PARAMETER) String password,
						@Query(COIN_PARAMETER) int coin);
	
	@GET(CHARACTER_PATH)
	public Collection<Character> getCharacters();
	
	@POST(BUY_PATH)
	public boolean buyCharacter(@Query(USERNAME_PARAMETER) String username,
								@Query(PASSWORD_PARAMETER) String password,
								@Query(CHARACTER_PARAMETER) int character);
	

}
