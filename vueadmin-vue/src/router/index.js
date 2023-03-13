import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Index from '../views/Index.vue'
import Menu from '../views/sys/Menu'
import Role from '../views/sys/Role'
import User from '../views/sys/User'

import axios from "../axios";
import store from "../store"

Vue.use(VueRouter)

// 解决同个Tabs标签页被重复点击而报错
const originalPush = VueRouter.prototype.push
// 修改 原型对象中的push方法
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}


const routes = [{
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '/index',
        name: 'Index',
        meta: {
            title: "首页"
        },
        component: Index
      },
      {
        path: '/sys/usercenter',
        name: 'UserCenter',
          meta: {
              title: '个人中心'
          },
        component: () => import('../views/sys/UserCenter.vue')
      },
      // {
      //   path: '/sys/menus',
      //   name: 'SysMenu',
      //   component: Menu
      // },
      // {
      //   path: '/sys/roles',
      //   name: 'SysRole',
      //   component: Role
      // },
      // {
      //   path: '/sys/users',
      //   name: 'SysUser',
      //   component: User
      // },
    ]
  },
  {
    path: '/Login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 导航守卫
// to：进入到哪个路由去 from：从哪个路由离开 next：路由的控制参数，常用的有next(true)和next(false)
router.beforeEach((to, from, next) => {

      let hasRoute = store.state.menus.hasRoute
      let token = localStorage.getItem("token")
      if (to.path == '/login') {
        // console.log("login!!!!!!!!!!!")
        next()
      } else if (!token) {
        // console.log("还没有token！！！")
        next({
          path: "/login"
        })
      } else if (to.path == '/' || to.path == '') {
        next({
          path: "/index"
        })
      } else if (token && !hasRoute) {
            axios.get("/sys/menu/nav", {
              headers: {
                Authorization: localStorage.getItem("token")
              }
            }).then(res => {
              console.log(res.data.data)
              // 拿到导航栏
              store.commit("setMenuList", res.data.data.nav)
              // 拿到用户权限
              store.commit("setPermList", res.data.data.authorities)
              // 动态绑定路由
              let newRoutes = router.options.routes;
              res.data.data.nav.forEach(menu => {
                  if (menu.children) {
                    menu.children.forEach(e => {
                      // 转成路由
                      let route = menuToRoute(e)
                      // 把路由添加到路由管理中
                      if (route) {
                        newRoutes[0].children.push(route)
                      }
                    })
                  }
                })
              console.log("oldRoutes---------------")
              console.log(newRoutes)
                // router.addRoutes(newRoutes)
              for (let x of newRoutes) {
                router.addRoute(x)
              }
              store.commit("changeRouteStatus", true)
              })
          }
          next()

      })

    // 导航转成路由
    const menuToRoute = (menu) => {
      if (!menu.component) {
        return null
      }
      let route = {
        name: menu.name,
        path: menu.path,
        meta: {
          icon: menu.icon,
          title: menu.title
        }
      }
      route.component = () => import('@/views/' + menu.component + '.vue')
      return route
    }

    export default router