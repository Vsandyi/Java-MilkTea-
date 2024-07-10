/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cofeeshop;

import java.sql.Date;

/**
 *
 * @author franc
 */
public class customersData {
    
    private Integer id;
    private Integer customersID;
    private Double total;
    private Date date;
    private String emUsername;
    
    public customersData(Integer id,Integer customersID,Double total,Date date,String emUsername){
        this.id = id;
        this.customersID = customersID;
        this.total = total;
        this.date = date;
        this.emUsername = emUsername;
        
    }
    public Integer getId(){
        return id;
    }
    public Integer getCustomersID(){
        return customersID;
    }
    public Double getTotal(){
        return total;
    }
    public Date getDate(){
        return date;
    }
    public String getEmUsername(){
        return emUsername;
    }
    
}
