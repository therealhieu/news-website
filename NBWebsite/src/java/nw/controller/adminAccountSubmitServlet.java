/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nw.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import nw.bean.AccountBean;
import nw.dao.AccountDAO;

/**
 *
 * @author Admin
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class adminAccountSubmitServlet extends HttpServlet {

    /**
     * Name of the directory where uploaded files will be saved, relative to the
     * web application directory.
     */
    /**
     * handles file upload
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Account Submit
        //Init

        HttpSession session = request.getSession();
        AccountDAO accDAO = new AccountDAO();
        AccountBean acc = new AccountBean();
        //get account info from rquest
        String para_action = request.getParameter("action");
        if (para_action != null) {
            int action = Integer.parseInt(para_action);
            //select action
            try {
                if (action == 1 || action == 2) {
                    acc.setUsername(request.getParameter("Username"));
                    acc.setPassword(request.getParameter("Password"));
                    acc.setFirstName(request.getParameter("FirstName"));
                    acc.setLastName(request.getParameter("LastName"));
                    acc.setEmail(request.getParameter("Email"));
                    acc.setPhone(request.getParameter("Phone"));
                    acc.setAccountTypeID(Integer.parseInt(request.getParameter("AccountTypeID")));
                    acc.setValid(Integer.parseInt(request.getParameter("Valid")));
                    String fakePath = request.getParameter("ProfilePicture1");                   
                    if ("".equals(fakePath)) {
                        acc.setProfilePicture("");
                    } else {
                        Part filePart = request.getPart("ProfilePicture");
                        //upload file
                        // gets absolute path of the web application
                        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                        String appPath = request.getServletContext().getRealPath("");
                        // constructs path of the directory to save uploaded file
                        //String savePath = appPath + File.separator +SAVE_DIR;
                        //get true project directory
                        String[] dir = appPath.split("build");
                        String savePath = dir[0] + "web\\img\\profilepic";
                        // creates the save directory if it does not exists
                        File fileSaveDir = new File(savePath);
                        if (!fileSaveDir.exists()) {
                            fileSaveDir.mkdir();
                        }
                        filePart.write(savePath + File.separator + fileName);
                        acc.setProfilePicture("img/profilepic/" + fileName);
                    }
                    int res = 0;
                    if (action == 1) {
                        //insert operation
                        res = accDAO.insertAccount(acc);
                        if (res == 0 || res == -1) {
                            session.setAttribute("AccountSubmitResult", "Failed to insert");
                        } else {
                            session.setAttribute("AccountSubmitResult", "Inserted successfully");
                            session.removeAttribute("ListAccount");
                        }
                    } else {//action ==2
                        //update operation
                        acc.setAccountID(Integer.parseInt(request.getParameter("AccountID")));
                        res = accDAO.updateAccount(acc);
                        if (res == 0 || res == -1) {
                            session.setAttribute("AccountSubmitResult", "Failed to update");
                        } else {
                            session.setAttribute("AccountSubmitResult", "Updated successfully");
                            session.removeAttribute("ListAccount");
                        }
                    }
                } else {//action ==3
                    int AccountID = Integer.parseInt(request.getParameter("AccountID"));
                    int deleteres = 0;
                    deleteres = accDAO.deleteAccountByID(AccountID);
                    if (deleteres == 0 || deleteres == -1) {
                        session.setAttribute("AccountSubmitResult", "Failed to delete");
                    } else {
                        session.setAttribute("AccountSubmitResult", "Deleted successfully");
                        session.removeAttribute("ListAccount");
                    }
                }

                response.sendRedirect(request.getContextPath() + "/manage/admin/account");
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(adminAccountSubmitServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
