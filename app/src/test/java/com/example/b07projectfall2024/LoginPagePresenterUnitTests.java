package com.example.b07projectfall2024;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

import android.content.Intent;

import com.example.b07projectfall2024.LoginPage.LoginActivityModel;
import com.example.b07projectfall2024.LoginPage.LoginActivityPresenter;
import com.example.b07projectfall2024.LoginPage.LoginActivityView;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPagePresenterUnitTests {
    @Mock
    LoginActivityView view;

    @Mock
    LoginActivityModel model;

    @Test
    public void TestLoginEmailEmpty(){
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.Login("", "123456");
        verify(view).SetErrorField("EmailError", "Email is required");
    }

    @Test
    public void TestLoginPasswordEmpty(){
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.Login("test@test.com", "");
        verify(view).SetErrorField("PasswordError", "Password is required");
    }

    @Test
    public void TestLoginShortPassword(){
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.Login("test@test.com", "123");
        verify(view).SetErrorField("PasswordError", "Password must be at least 6 characters in length");
    }

    @Test
    public void TestLoginSuccessfulLogin(){
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.Login("test@test.com", "123456");
        verify(model).LoginUser(presenter, "test@test.com", "123456");
    }

    @Test
    public void TestPageRedirect(){
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        Intent tmpIntent = presenter.PageRedirect(LoginActivityView.class);
        verify(view).startActivity(tmpIntent);
    }
}