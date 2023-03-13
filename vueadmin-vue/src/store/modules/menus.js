import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default {
    state: {
        menuList: [],   // 菜单数据
        permList: [],   // 用户权限列表
        hasRoute: false,
        editableTabsValue: '2',
        editableTabs: [{
            title: '首页',
            name: 'Index'
        }]
    },
    mutations: {
        setMenuList(state,menus){
            state.menuList = menus
        },
        setPermList(state,perms){
            state.permList = perms
        },
        changeRouteStatus(state,hasRoute) {
            state.hasRoute = hasRoute

            sessionStorage.setItem("hasRoute",hasRoute)
        },
        addTabs(state, tab) {
            console.log(tab)
            // 判断是否在栈内
            let index = state.editableTabs.findIndex(item => item.name === tab.name)
            if (index === -1) {
                // 添加到tabs中
                state.editableTabs.push(tab)
            }
            // 当前激活的tab
            state.editableTabsValue = tab.name
        },
        setActiveTab(state, tabName) {
            state.editableTabsValue = tabName
        },
        resetState:(state)=>{
            state.menuList = []
            state.permList = []
            state.hasRoute = false
            state.editableTabsValue = 'Index'
            state.editableTabs = [{
                title: '首页',
                name: 'Index'
            }]
        }
    },
    actions: {
    }
}
