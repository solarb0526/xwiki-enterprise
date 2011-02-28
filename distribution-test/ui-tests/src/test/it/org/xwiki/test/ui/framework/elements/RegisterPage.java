/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.test.ui.framework.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the actions possible on the Registration Page
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class RegisterPage extends BasePage
{
    @FindBy(id = "register")
    private WebElement registerFormElement;

    @FindBy(xpath = "//form[@id='register']/div/span/input[@type='submit']")
    private WebElement submitButton;

    private FormElement form;

    /** To put the registration page someplace else, subclass this class and change this method. */
    public void gotoPage()
    {
        getUtil().gotoPage("XWiki", "Register", "register");
    }

    public void fillInJohnSmithValues()
    {
        fillRegisterForm("John", "Smith", "JohnSmith", "WeakPassword", "WeakPassword", "johnsmith@xwiki.org");
    }

    public void fillRegisterForm(final String firstName, final String lastName, final String username,
        final String password, final String confirmPassword, final String email)
    {
        Map<String, String> map = new HashMap<String, String>();
        if (firstName != null) {
            map.put("register_first_name", firstName);
        }
        if (lastName != null) {
            map.put("register_last_name", lastName);
        }
        if (username != null) {
            map.put("xwikiname", username);
        }
        if (password != null) {
            map.put("register_password", password);
        }
        if (confirmPassword != null) {
            map.put("register2_password", confirmPassword);
        }
        if (email != null) {
            map.put("register_email", email);
        }
        getForm().fillFieldsByName(map);
        // There is a little piece of js which fills in the name for you.
        // This causes flickering if what's filled in is not cleared.
        if (username != null) {
            while (!username.equals(getForm().getFieldValue(By.name("xwikiname")))) {
                getForm().setFieldValue(By.name("xwikiname"), username);
            }
        }
    }

    private FormElement getForm()
    {
        if (this.form == null) {
            this.form = new FormElement(this.registerFormElement);
        }
        return this.form;
    }

    public void clickRegister()
    {
        this.submitButton.click();
    }

    /** @return a list of WebElements representing validation failure messages. Use after calling register() */
    public List<WebElement> getValidationFailureMessages()
    {
        return getDriver().findElements(By.xpath("//dd/span[@class='LV_validation_message LV_invalid']"));
    }

    /** @return Is the specified message included in the list of validation failure messages. */
    public boolean validationFailureMessagesInclude(String message)
    {
        for (WebElement messageElement : getValidationFailureMessages()) {
            if (messageElement.getText().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public boolean liveValidationEnabled()
    {
        return !getDriver().findElements(By.xpath("/html/body/div/div/div[3]/div/div/div/div/div/script")).isEmpty();
    }

    /** Try to make LiveValidation validate the forms. Focus on an unvalidated field (register_first_name) */
    public void triggerLiveValidation()
    {
        // By manually invoing insubmit with null as it's parameter,
        // liveValidation will check fields but when it attempts to call submit with null as the
        // input, it encounters an error which keeps the next page from loading.
        executeJavascript("try{ document.getElementById('register').onsubmit(null); }catch(err){}");
    }
}
