import { div, a, h3, h1,h5,p,button } from "./createElement.js";

function myGenerateModal(id,title,body,footer_1,footer_2){
    let exists = document.getElementById(id)
    let bodyLabel = id+"body"
    if (exists){
         let bodyl = document.getElementById(bodyLabel)
         bodyl.replaceChildren(body)

    }else{
        let label = id+"Lable"

         const content = div(
                  { class: "modal fade",
                   id: id,
                   tabindex: "-1",
                   "aria-labelledby": label,
                    "aria-hidden": "true"
                   },
                  div(
                  {class:"modal-dialog"},
                    div({class:"modal-content"},
                        div({class:"modal-header"},
                            h5(title,{class:"modal-title",id:label}),
                            button({ type:"button", class:"btn-close" , "data-bs-dismiss":"modal", "aria-label":"Close" }
                            )
                        ),
                        div({class:"modal-body",id:bodyLabel},
                            body
                        ),
                        div({class:"modal-footer"},
                        footer_1,
                        footer_2

                        )
                    )
                  )
                );
        document.body.appendChild(content);
    }
}

export default {
  myGenerateModal,
};