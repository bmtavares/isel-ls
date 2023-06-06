import { div, h3, p } from "./createElement.js";
import user from "../user.js";
import {generateFormModal}  from "./formGenerator.js";
const API_BASE_URL = "http://localhost:9000/";
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
        if (dataForm.password !== dataForm.passwordconfirmation) {

        } else {
            delete dataForm.passwordconfirmation
            const data = JSON.stringify(dataForm)

            const rsp = await fetch(API_BASE_URL + `users`, {
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
    }
  const fields = [
      { type: "text", name: "name", required: true },
      { type: "text", name: "email", required: true },
      { type: "text", name: "password", required: true },
      { type: "text", name: "passwordConfirmation", required: true },
  ]

  return generateFormModal(
      "signUp",
      registerNewUser,
      fields,
      "Sign Up"
  )

}

export default {
  listingCard,
  listing,
  signUpContent
};
