/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nw.dao;

import java.sql.*;
import java.util.*;
import java.lang.*;
import static java.lang.Class.forName;
import nw.bean.AccountBean;
import static nw.jdbc.DBconnect.getConnection;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class AccountDAO {

    public ArrayList<AccountBean> getAccount() throws SQLException, ClassNotFoundException {
        try {
            ArrayList<AccountBean> alAccount = new ArrayList<>();
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select* from Account;");
            while (rs.next()) {
                AccountBean acc = new AccountBean();
                acc.setAccountID(rs.getInt("AccountID"));
                acc.setAccountTypeID(rs.getInt("AccountTypeID"));
                acc.setDateCreated(rs.getString("DateCreated"));
                acc.setEmail(rs.getString("Email"));
                acc.setFirstName(rs.getString("FirstName"));
                acc.setLastName(rs.getString("LastName"));
                acc.setLastModified(rs.getString("LastModified"));
                acc.setPassword(rs.getString("Password"));
                acc.setPhone(rs.getString("Phone"));
                acc.setUsername(rs.getString("Username"));
                acc.setProfilePicture(rs.getString("ProfilePicture"));
                acc.setValid(rs.getInt("Valid"));
                boolean add = alAccount.add(acc);
            }
            return alAccount;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public int insertAccount(AccountBean acc) throws SQLException, ClassNotFoundException {
        try {
            Connection conn = getConnection();
            String hashedpw = BCrypt.hashpw(acc.getPassword(), BCrypt.gensalt(15));
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `Account` (`Username`, `Password`, `FirstName`, `LastName`, `Email`, `Phone`, `AccountTypeID`,`ProfilePicture`, `Valid`) VALUES (?,?,?,?,?,?,?,?,?)");
            ps.setString(1, acc.getUsername());
            ps.setString(2, hashedpw);
            ps.setString(3, acc.getFirstName());
            ps.setString(4, acc.getLastName());
            ps.setString(5, acc.getEmail());
            ps.setString(6, acc.getPhone());
            ps.setInt(7, acc.getAccountTypeID());
            ps.setString(8, acc.getProfilePicture());
            ps.setInt(9, acc.getValid());
            System.out.println(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.toString());
            return -1;
        }
    }

    public int getAccountByPassword(String inusername, String inpassword, AccountBean account) throws ClassNotFoundException {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("Select* from Account where Username = ?");
            ps.setString(1, inusername);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                return 0;
            } else {
                String outpw = rs.getString("Password");
                if (BCrypt.checkpw(inpassword, outpw)) {
                    account.setAccountID(rs.getInt("AccountID"));
                    account.setAccountTypeID(rs.getInt("AccountTypeID"));
                    account.setDateCreated(rs.getString("DateCreated"));
                    account.setEmail(rs.getString("Email"));
                    account.setFirstName(rs.getString("FirstName"));
                    account.setLastName(rs.getString("LastName"));
                    account.setLastModified(rs.getString("LastModified"));
                    account.setPassword(outpw);
                    account.setPhone(rs.getString("Phone"));
                    account.setUsername(rs.getString("Username"));
                    account.setProfilePicture(rs.getString("ProfilePicture"));
                    account.setValid(rs.getInt("Valid"));                   
                } else {
                    return 0;
                }

            }
            return 1;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return -1;
        }
    }
    public int deleteAccountByID(int AccountID) throws ClassNotFoundException{
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("delete from Account where AccountID = ?");
            ps.setInt(1, AccountID);
           return ps.executeUpdate();
           
        } catch (SQLException e) {
            System.out.println(e.toString());
            return -1;
        }
    }
}