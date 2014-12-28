package com.beiluoshimen.securityguard.tools;
//
//public class PkgSizeObserver extends IPackageStatsObserver.{  
//    /*** 回调函数， 
//     * @param pStatus ,返回数据封装在PackageStats对象中 
//     * @param succeeded  代表回调成功 
//     */   
//    @Override  
//    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)  
//            throws RemoteException {  
//        // TODO Auto-generated method stub  
//       cachesize = pStats.cacheSize  ; //缓存大小  
//        datasize = pStats.dataSize  ;  //数据大小   
//        codesize = pStats.codeSize  ;  //应用程序大小  
//        totalsize = cachesize + datasize + codesize ;  
//        Log.i(TAG, "cachesize--->"+cachesize+" datasize---->"+datasize+ " codeSize---->"+codesize)  ;  
//    }  
//}  