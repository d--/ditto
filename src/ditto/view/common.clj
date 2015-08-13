(ns ditto.view.common
  "Provides a common layout to be used by all pages."
  (:use [hiccup.core]
        [hiccup.page]))

(defn layout [title inner-html]
  "A common layout to be used by all pages.  Title will be used as the page
  title and as the top navigation bar's highlighted link."
  (let [home? (= title "Home")]
    (html5 {:lang "en"}
      [:head
        [:title title]
        (include-css "/ditto-static/css/bootstrap.min.css")
        (include-css "/ditto-static/css/style.css")
        (include-js "http://code.jquery.com/jquery-latest.js")
        (include-js "/ditto-static/js/bootstrap.min.js")]
      [:body
        [:div.navbar
          [:div.navbar-inner
            [:a.brand {:href "/ditto-ui/"} "ditto"]
            [:ul.nav
              [:li {:class (when home? :active)}
                [:a {:href "/ditto-ui/"} "Home"]]
              (when (not home?)
                [:li.active
                  [:a {:href "#"} title]])]]]
        inner-html])))
