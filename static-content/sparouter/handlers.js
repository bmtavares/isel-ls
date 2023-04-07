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

const API_BASE_URL = "http://localhost:9000/"

function getHome(){
    const mainContent = document.getElementById("mainContent");
    const h1 = document.createElement("h1")
    const text = document.createTextNode("Home")
    h1.appendChild(text)
    mainContent.replaceChildren(h1)
}

function getStudents(mainContent){
    fetch(API_BASE_URL + "user")
        .then(res => res.json())
        .then(students => {
            const div = document.createElement("div")

            const h1 = document.createElement("h1")
            const text = document.createTextNode("Students")
            h1.appendChild(text)
            div.appendChild(h1)

            students.forEach(s => {
                const p = document.createElement("p")
                const a = document.createElement("a")
                const aText = document.createTextNode("Link Example to User/" + s.number);
                a.appendChild(aText)
                a.href="#User/" + s.number
                p.appendChild(a)
                div.appendChild(p)
            })
            mainContent.replaceChildren(div)
        })
}
function boardApend(item){
const ulStd = document.createElement("ul")
                   const liName = document.createElement("li")
                   const textName = document.createTextNode("Name : " + item.name)


                   liName.appendChild(textName)
                   ulStd.appendChild(liName)
/*
var temp_link = document.createElement("a");
temp_link.href = "http://localhost:9000/sparouter/index.html#boards/"+item.id;
temp_link.target = '_blank';
temp_link.innerHTML = "link";


var par = document.createElement("p");
par.innerHTML = "details: ";
par.appendChild(temp_link);
*/

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

// Optionally, you can also listen for changes to the URL and update the link's href accordingly
window.addEventListener('popstate', function(event) {
  temp_link.href = window.location.pathname + "#boards/" + item.id;
});


par.appendChild(temp_link);
                   mainContent.appendChild(ulStd)
                   mainContent.appendChild(par)

}


function getBoardDetail(mainContent){
var url =  window.location.href
url = url.split('#')[1];
const boardid =  url.split('/')[1]
fetch(API_BASE_URL + "boards/"+boardid,  {headers: {Authorization: 'Bearer c6705375-9fa7-4987-97f8-e8cdf7b4cb62'}})
.then(res => res.json())
        .then(user => {
            const ulStd = document.createElement("ul")

            const liName = document.createElement("li")
            const textName = document.createTextNode("Name : " + user.name)
            liName.appendChild(textName)

            const liNumber = document.createElement("li")
            const textNumber = document.createTextNode("description : " + user.description)
            liNumber.appendChild(textNumber)

            ulStd.appendChild(liName)
            ulStd.appendChild(liNumber)

            mainContent.replaceChildren(ulStd)
        }
)

// alter links





}

function getBoards(mainContent){
 fetch(API_BASE_URL + "boards" ,  {headers: {Authorization: 'Bearer c6705375-9fa7-4987-97f8-e8cdf7b4cb62'}
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



                                           })
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
    })
}


export const handlers = {
    getHome,
    getUser,
    getBoards,
    getBoardDetail,
}

export default handlers