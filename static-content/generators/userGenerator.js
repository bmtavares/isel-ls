import {button, div, h3, p} from "./createElement.js";
import user from "../user.js";
import {generateFormModal}  from "./formGenerator.js";

import appConstants from "../appConstants.js";

function listingCard(user) {
  return div(
    {
      class: "card",
    },
    div(
      {
        class: "card-body",
      },
      h3(`${user.name}`, {
        class: "card-title",
      }),
      p(`✉ ${user.email}`, {
        class: "card-text",
      })
    )
  );
}

function listing(users) {
  return div(
    { class: "row gy-2" },
    ...users.map((user) => div({ class: "col-lg-3" }, listingCard(user)))
  );
}

function signUpContent(authHeader){
    async function registerNewUser(e) {
        e.preventDefault();
        const formData = new FormData(e.target); // Using the FormData object we can quickly fetch all the inputs
        const dataForm = Object.fromEntries(formData.entries())
        if (dataForm.password !== dataForm.passwordConfirmation) {
           const alertPwMismatch = div(
               "Passwords Don't Match",
            {class:"alert alert-danger alert-dismissible fade show",role:"alert"},
               button(
                   {class:"btn-close","data-bs-dismiss":"alert",type:"button"}
               )
           )
            document.querySelector("#signUpContainer").appendChild(alertPwMismatch)
        } else {
            delete dataForm.passwordConfirmation
            const data = JSON.stringify(dataForm)

            const rsp = await fetch(`${appConstants.API_BASE_URL}`+ `users`, {
                headers: {...authHeader, "Content-Type": "application/json"},
                method: "post",
                body: data
            })
                const userInfo = await rsp.json()
                if (rsp.status !== 200 && rsp.status !== 201){
                    const errorAlert = div(
                        userInfo,
                        {class:"alert alert-danger alert-dismissible fade show",role:"alert"},
                        button(
                            {class:"btn-close","data-bs-dismiss":"alert",type:"button"}
                        )
                    )
                    document.querySelector("#signUpContainer").appendChild(errorAlert)
                }else {
                    user.setUser(userInfo.token, userInfo.id, userInfo.name)
                    location.hash="#home"
                }
        }
    }
  const fields = [
      { type: "text", name: "name", required: true,label:"Username" },
      { type: "text", name: "email", required: true,label:"Email" },
      { type: "text", name: "password", required: true,label:"Password" },
      { type: "text", name: "passwordConfirmation", required: true,label:"Password Confirmation" },
  ]

  return div(
      { class: "container mt-4",id:"signUpContainer"},
      generateFormModal(
          "signUp",
          registerNewUser,
          fields,
          "Sign Up"
      )
  )
}

function loginFormContent(authHeader){
    async function loginRequest(e) {
        e.preventDefault();
        const formData = new FormData(e.target); // Using the FormData object we can quickly fetch all the inputs
        const dataForm = Object.fromEntries(formData.entries())
        const data = JSON.stringify(dataForm)

            const rsp = await fetch(`${appConstants.API_BASE_URL}` + `session`, {
                headers: {...authHeader, "Content-Type": "application/json"},
                method: "post",
                body: data
            })
            try {
                const userInfo = await rsp.json()
                user.setUser(userInfo.token, userInfo.id, userInfo.name)
                location.hash="#home"
            } catch (e) {

            }
        }
    const fields = [
        { type: "text", name: "email", required: true,label:"Email" },
        { type: "text", name: "password", required: true,label:"Password" },
    ]

    return div(
        { class: "container mt-4" },
        generateFormModal(
            "login",
            loginRequest,
            fields,
            "Login"
        )
    )
}

export default {
  listingCard,
  listing,
  signUpContent,
    loginFormContent
};
