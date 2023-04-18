/*
This example creates the students views using directly the DOM Api
But you can create the views in a different way, for example, for the student details you can:
    createElement("ul",
        createElement("li", "Name : " + student.name),
        createElement("li", "Number : " + student.number)
    )
or
    ul(
        li("Name : " + student.name),
        li("Number : " + student.name)
    )
Note: You have to use the DOM Api, but not directly
*/

import * as c from "./createElement.js"
const API_BASE_URL = "http://localhost:9000/"


function createLinks(links) {
const links_el = document.getElementById("links")
links_el.innerHTML = "";

  links.forEach((link) => {
    const anchor = document.createElement("a");
    anchor.href = link.href;
    anchor.className = `link-${link.type}`;
    anchor.innerText = link.text;

    links_el.appendChild(anchor);
  });

}

//_-------------------//
function getHome(mainContent){
    const h1 = document.createElement("h1")
    const text = document.createTextNode("Home")
    h1.appendChild(text)
    mainContent.replaceChildren(h1)
     const links = [
                      { href: "#userDetails", type: "primary", text: "user details" },
                      { href: "#boards", type: "primary", text: "Boars" },
                    ];

                    createLinks(links);

}
function getUser(mainContent){
    fetch(API_BASE_URL + "users/1"
   )    .then(res => res.json())
        .then(user => {
            const ulStd = document.createElement("ul")

            const liName = document.createElement("li")
            const textName = document.createTextNode("Name : " + user.name)
            liName.appendChild(textName)

            const liNumber = document.createElement("li")
            const textNumber = document.createTextNode("Number : " + user.email)
            liNumber.appendChild(textNumber)

            ulStd.appendChild(liName)
            ulStd.appendChild(liNumber)

            mainContent.replaceChildren(ulStd)

            const links = [
                                  { href: "#home", type: "secondary", text: "Home" },
                                  { href: "#boards", type: "primary", text: "Boars" },
                                ];

                                createLinks(links);


    })
}
//-----------------board-----------//

function boardApend(item){
            const ulStd = document.createElement("ul")
                   const liName = document.createElement("li")
                   const textName = document.createTextNode("Name : " + item.name)


                   liName.appendChild(textName)
                   ulStd.appendChild(liName)

                var temp_link = document.createElement("a");
                temp_link.href = "/sparouter/index.html#boards/"+item.id; // modify the URL path to include the board ID
                temp_link.innerHTML = "link";

                var par = document.createElement("p");
                par.innerHTML = "details: ";
                par.appendChild(temp_link);

                temp_link.addEventListener('click', function(event) {
                  event.preventDefault(); // prevent the link from navigating away from the page
                  window.history.pushState(null, null, temp_link.href); // update the URL without navigating
                  location.reload();
                });
/*
                // Optionally, you can also listen for changes to the URL and update the link's href accordingly
                window.addEventListener('popstate', function(event) {
                  temp_link.href = window.location.pathname + "#boards/" + item.id;
                });

*/
                par.appendChild(temp_link);
                                   mainContent.appendChild(ulStd)
                                   mainContent.appendChild(par)

}


function getBoards(mainContent){
 fetch(API_BASE_URL + "boards" ,  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}
                                       })
                                       .then(res => res.json())
                                       .then(boards => {
                                                   const ulStd = document.createElement("ul")
                                                   const lititle = document.createElement("li")

                                                   const textTitle = document.createTextNode("__BOARDS __")
                                                    ulStd.appendChild(lititle)

                                                     lititle.appendChild(textTitle)
                                                     mainContent.replaceChildren(ulStd)
                                                   boards.forEach(boardApend);

                    const links = [
                      { href: "#home", type: "secondary", text: "Home" },
                      { href: "#userDetails", type: "primary", text: "user details" },
                    ];

                    createLinks(links);

                                                           })
}

//-----board detail ---//
function addListsToPage(item_list){
       const ulStd = document.createElement("ul")
                         const liName = document.createElement("li")
                         const textName = document.createTextNode("Name : " + item_list.name)


                         liName.appendChild(textName)
                         ulStd.appendChild(liName)

                      var temp_link = document.createElement("a");
                      temp_link.href = "/sparouter/index.html#boards/"+item_list.boardId+"/lists/"+item_list.id; // modify the URL path to include the board ID
                      temp_link.innerHTML = "link";

                      var par = document.createElement("p");
                      par.innerHTML = "details: ";
                      par.appendChild(temp_link);

                      temp_link.addEventListener('click', function(event) {
                        event.preventDefault(); // prevent the link from navigating away from the page
                        window.history.pushState(null, null, temp_link.href); // update the URL without navigating
                        location.reload();
                      });
      /*
                     // Optionally, you can also listen for changes to the URL and update the link's href accordingly
                      window.addEventListener('popstate', function(event) {
                        temp_link.href = window.location.pathname + "#boards/" + item.id;
                      });

     */
                      par.appendChild(temp_link);
                                         mainContent.appendChild(ulStd)
                                         mainContent.appendChild(par)

}

function getBoardDetail(mainContent,kargs){
    const boardid =  kargs.b_id
    fetch(API_BASE_URL + "boards/"+boardid,  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
    .then(res => res.json())
            .then(board => {
                const ulStd = document.createElement("ul")

                const liName = document.createElement("li")
                const textName = document.createTextNode("Name : " + board.name)
                liName.appendChild(textName)

                const liNumber = document.createElement("li")
                const textNumber = document.createTextNode("description : " + board.description)
                liNumber.appendChild(textNumber)
                ulStd.appendChild(liName)
                ulStd.appendChild(liNumber)
                mainContent.replaceChildren(ulStd)

                fetch(API_BASE_URL + "boards/"+boardid+"/lists",  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
                    .then(res => res.json())
                        .then(lists =>{
                            console.log(lists)
                            lists.forEach(addListsToPage)

                        }
                        )



                const links = [
                  { href: "#boards", type: "secondary", text: "boards" },
                  { href: "#board/"+boardid+"/user-list", type: "primary", text: "board_useres" },
                  { href: "#List_details", type: "secondary", text: "List_details" },
                ];

                createLinks(links);
                        }


            )


}
//--- board user ---//

function userApend(item){
        const mainContent = document.getElementById("mainContent")
       const ulStd = document.createElement("ul")
       const liName = document.createElement("li")
       const textName = document.createTextNode("Name : " + item.name)
       const textemail = document.createTextNode("Email : " + item.email)

       liName.appendChild(textName)
       liName.appendChild(textemail)

       ulStd.appendChild(liName)
        mainContent.appendChild(ulStd)
}

function getBoardsUsers(mainContent,kargs){
    const boardid =  kargs.b_id
    fetch(API_BASE_URL + "boards/"+boardid+"/user-list",  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
    .then(res => res.json())
            .then(user => {
            mainContent.innerHTML = "";
            user.forEach(userApend);

        const links = [
          { href: "#boards/"+boardid, type: "secondary", text: "boards details" },
        ];

        createLinks(links);
                }
    )
}

//-- list details --//
function addCardsToPage(card){
const mainContent = document.getElementById("mainContent")
const main = c.div(c.h3("card: "+ card.name,))

//main.appendChild(c.a(card.name,{ href: "/sparouter/index.html#boards/"+card.boardId+"/lists/"+card.listId+"/cards/"+card.id }))

var temp_link = document.createElement("a");
temp_link.href = "/sparouter/index.html#boards/"+card.boardId+"/lists/"+card.listId+"/cards/"+card.id
temp_link.innerHTML = card.name;
main.appendChild(temp_link)

mainContent.appendChild(main)
}

function listDetails(mainContent,kargs){
const boardid =  kargs.b_id
const listid =  kargs.l_id
 fetch(API_BASE_URL + "boards/"+boardid+"/lists/"+listid,  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
    .then(res => res.json())
    .then(list => {
    mainContent.innerHTML = "";
        const main = c.div(c.h1("lista: "+ list.name,))
        mainContent.appendChild(main)
        fetch(API_BASE_URL + "boards/"+boardid+"/lists/"+listid+"/cards",  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
        .then(res => res.json())
        .then(cards =>
        cards.forEach(addCardsToPage)
        )
         const links = [
                  { href: "#boards/"+boardid, type: "secondary", text: "boards details" },
                ];

                createLinks(links);


        }
    )
}
// ---------- cards -------- //

function cardDetail(mainContent,kargs){
const boardid =  kargs.b_id
const listid =  kargs.l_id
const cardid =  kargs.c_id
fetch(API_BASE_URL + "boards/"+boardid+"/cards/"+cardid,  {headers: {Authorization: 'Bearer f52129ca-ccf1-42cc-a363-fdc89f71901b'}})
.then(res => res.json())
    .then(card => {
    mainContent.innerHTML = "";
    var date = new Date(card.dueDate )
    const main = c.div(c.h1("card: "+ card.name,),
        c.li(card.description),
        c.li(`due date: ${date.getFullYear()}  ${date.getMonth()} ${date.getDate()}`)
    )
            mainContent.appendChild(main)
 const links = [
                  { href: "#boards/"+boardid+"/lists/"+listid, type: "secondary", text: "list details" },
                ];

                createLinks(links);

    })
}




export const handlers = {
    getHome,
    getUser,
    getBoards,
    getBoardDetail,
    getBoardsUsers,
    listDetails,
    cardDetail,
}

export default handlers