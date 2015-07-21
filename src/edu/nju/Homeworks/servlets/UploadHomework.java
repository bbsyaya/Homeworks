package edu.nju.Homeworks.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import edu.nju.Homeworks.dao.FileDao;
import edu.nju.Homeworks.dao.FileDaoImpl;
import edu.nju.Homeworks.model.FileBean;
import edu.nju.Homeworks.model.UserBean;

/**
 * Servlet implementation class UploadHomework
 */
@WebServlet("/UploadHomework")
public class UploadHomework extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadHomework() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		FileItemFactory factory=new DiskFileItemFactory();
		ServletFileUpload upload=new ServletFileUpload(factory);
		String realDirectory = request.getSession().getServletContext().getRealPath("/"); 
		String info=null;
		String[] split = null;
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> list=upload.parseRequest(request);
			for(FileItem item: list){
				if(item.isFormField()){   //判断FileItem是一个文件上传对象还是普通表单对象
					info=item.getString("UTF-8");
					split=info.split(" ");
				}
				else{
					 String lastpath = item.getName();//获取上传文件的名称  
					 lastpath = lastpath.substring(lastpath.lastIndexOf(".")); 
					 if(lastpath.equals(split[1])){
					 String filename = UUID.randomUUID().toString().replace("-", "") + lastpath;  
					 try {
						item.write(new File(realDirectory+filename));
				   	} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					               } 
					 HttpSession session=request.getSession();
					 session.setAttribute("hmk_submit", "success");
					 UserBean ub=(UserBean)session.getAttribute("user");
					 FileDao fd=new FileDaoImpl();
					 fd.updateHomeworkFile(new FileBean(split[2], Integer.parseInt(split[0]), ub.getName(), filename));
					 }
					 else{
						 HttpSession session=request.getSession();
						 session.setAttribute("hmk_submit", "fail");
					 }
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.sendRedirect("/Homeworks/jsp/student_homework_submit.jsp");
	}

}
