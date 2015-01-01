package com.beiluoshimen.securityguard.market;


import retrofit.http.GET;
import retrofit.http.Query;


public interface DlSvcApi {
	
	public static final String NO_PARAMETER = "no";
	
	public static final String DL_SVC_PATH = "/download";

	@GET(DL_SVC_PATH)
	public DlData dlCharacter(
			@Query(NO_PARAMETER) int no);
	
}
