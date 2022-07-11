package com.example.myapplication.Controller;

import android.content.Context;

import com.example.myapplication.Dao.UserDao;
import com.example.myapplication.entity.User;

public class UserController {
    private UserDao userDao;
    private Context my_context;

    public UserController(Context context){
        my_context = context;
        userDao = new UserDao(my_context);
    }

    /*功能:验证用户是否输入了正确的用户名和密码
     *参数:用户的用户名 用户的密码
     *返回值:成功则返回用户id,失败则返回-1
     */
    public int UserAuth(String username,String password){
        userDao.openDB();
        return userDao.authUser(username,password);
    }

    /*功能:添加新用户
     *参数:用户的用户名 用户的密码
     *返回值:成功则返回行数,失败则返回-1
     */
    public long AddNewUser(String username,String password){
        User new_user = new User();
        new_user.username = username;
        new_user.password = password;
        userDao.openDB();
        return userDao.addUser(new_user);
    }

    /*功能:根据id返回用户实体类
     *参数:用户id
     *返回值:用户实体类
     */
    public User getUser(int userId){
        return userDao.findUser(userId);
    }

    /*功能:修改用户信息
     *参数:用户实体类
     *返回值:成功返回非负值，不成功则返回-1
     */
    public int modifyUser(User user){return userDao.modifyUser(user);}
}
