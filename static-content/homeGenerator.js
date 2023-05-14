import * as c from "./createElement.js";

function footer() {
  return c.footer(
    {
      class: "fixed-bottom isel-bg-colour ms-1 mb-1 me-1 rounded",
    },
    c.ul(
      {
        class: "nav justify-content-center",
      },
      c.li(
        {
          class: "nav-item",
        },
        c.a("Made in Chelas by", {
          class: "nav-link text-body text-opacity-70",
        })
      ),
      c.li(
        {
          class: "nav-item",
        },
        c.a("Manuel Fonseca", {
          class: "nav-link link-light",
          href: "https://github.com/manuel-48052",
        })
      ),
      c.li(
        {
          class: "nav-item",
        },
        c.a("Sérgio Zorro", {
          class: "nav-link link-light",
          href: "https://github.com/sergiomiguelzorro",
        })
      ),
      c.li(
        {
          class: "nav-item",
        },
        c.a("Bruno Tavares", {
          class: "nav-link link-light",
          href: "https://github.com/bmtavares",
        })
      )
    )
  );
}

function content(token) {
  return c.div(
    {
      class: "container-fluid text-center",
    },
    c.div(
      {
        class: "row justify-content-center",
      },

      c.h1("Welcome to LEIC Fauxllo!"),
      c.p(
        token
          ? "You appear to be logged in!"
          : "Look's like you're not logged in..",
        {
          class: "text-muted",
        }
      )
    )
  );
}

export default {
  content,
  footer,
};
