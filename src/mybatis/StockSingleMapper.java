package mybatis;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dao.StockSingle;

public interface StockSingleMapper {
	
	 /** 
     * 获得单个用户 
     * @param userid 
     * @return 
     */  
    public List<StockSingle> getAllStockSingle();  
    /** 
     * 获得一个用户集合 
     * @param userids 
     * @return 
     */  
    public List<String> getStockSingleAllFullId();  
    /** 
     * 删除一个用户 
     * @param userid 
     */  
   // public void deleteUserInfo(String userid);  
    /** 
     * 添加一个用户 
     * @param userinfo 
     */  
  //  public void addUserInfo(StockSingle userinfo);  
    /** 
     * 修改用户 
     * @param userinfo 
     * @return 
     */  
 //   public void updateUserInfo(StockSingle userinfo);  
    /** 
     * 翻页查询 
     * @param currpage 
     * @param pageSize 
     * @param conds 
     * @return 
     */  
   // public List<StockSingle> getUserInfoPage(int currpage,int pageSize,Map<String,Object> conds);  
    
}
