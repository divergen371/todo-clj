(ns todo-clj.core
  (:require [ring.adapter.jetty :as server]))
(defonce server (atom nil))

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    "<h1>Hello, World</h1>"})

(defn start-server []
  (when-not @server
    (reset! server (server/run-jetty #'handler {:port 3000 :join? false}))))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))


(defn ok [body]
  {:status 200
   :body   body})

(defn html [res]
  (assoc res :headers {"Content-Type" "text/html: charset=utf-8"}))

(defn not-found []
  {:status 404
   :body   "<h4>404 page not found</h1>"})

(defn home-view [req]
  "<h1>ホーム画面</h1>
  <a href=\"/todo \">TODO 一覧</a>")

(defn home [req]
  (-> (home-view req)
      ok
      html))

(def todo-list
  [{:title "朝ご飯を作る"}
   {:title "燃えるゴミを出す"}
   {:title "卵を買って変える"}
   {:title "お風呂を洗う"}])

(defn todo-index-view [req]
  `("<h1>TODO 一覧</h1>"
     "<ul>"
     ~@(for [{:keys [title]} todo-list]
         (str "<li>" title "</li>"))
     "</ul>"))


(defn todo-index [req]
  (-> (todo-index-view req)
      ok
      html
      ))



(def routes
  {"/"     home
   "/todo" todo-index
   })

(defn match-roure [uri]
  (get routes uri))

(defn handler [req]
  (let [uri (:uri req)
        maybe-fn (match-roure uri)]
    (if maybe-fn
      (maybe-fn req)
      (not-found))))
