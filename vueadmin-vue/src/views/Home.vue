<template>
    <el-container>
        <el-aside width="200px">
            <SideMenu></SideMenu>
        </el-aside>
        <el-container>
            <el-header>
                <strong>VueAdmin后台管理系统</strong>
                <div class="header-avatar">
                    <el-avatar size="medium" :src="userInfo.avatar"></el-avatar>
                    <el-dropdown>
                        <span class="el-dropdown-link">
                            {{userInfo.username}}
                            <i class="el-icon-arrow-down el-icon--right"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item>
                                 <router-link :to="{name:'UserCenter'}">个人中心</router-link>
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="logout">退出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>

                    <el-link style="color: #fff" href="http://www.baidu.com" target="_blank">技术</el-link>
                    <el-link style="color: #fff" href="http://www.baidu.com" target="_blank">关于</el-link>
                </div>
            </el-header>
            <el-main>
                <Tabs></Tabs>
                <div style="margin:0 15px;">
                    <router-view></router-view>
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
    import SideMenu from "./inc/SideMenu";
    import Tabs from "./inc/Tabs";
    export default {
        name: "Home",
        components:{
            SideMenu,Tabs
        },
        data(){
            return {
                userInfo:{
                    id:"",
                    username:"",
                    avatar:""
                }
            }
        },
        created() {
            this.getUserInfo()
        },
        methods:{
            getUserInfo() {
                this.$axios.get("/sys/userInfo").then(res =>{
                    this.userInfo = res.data.data
                })
            },
            logout() {
                this.$axios.post("/logout").then(res=> {
                    localStorage.clear();
                    sessionStorage.clear();
                    //清除用户信息
                    this.$store.commit("resetState")
                    //跳转到登录页面
                    this.$router.push("/login")
                })
            }
        }
    }
</script>

<style scoped>
    .el-container {
        padding: 0;
        margin: 0;
        height: 100%;
    }

    .header-avatar{
        float: right;
        width: 210px;
        display: flex;
        justify-content: space-around;
        align-items: center;
    }

    .el-header {
        background-image: linear-gradient(-45deg, #0081ff, #1cbbb4);
        color: #333;
        font-size: 30px;
        font-family: "Corbel Light";
        text-align: center;
        line-height: 60px;
        user-select: none;
    }

    .el-aside {
        background-color: #D3DCE6;
        color: #333;
        line-height: 200px;
    }

    .el-main {
        color: #333;
        padding: 0;
    }

    a{
        text-decoration: none;
    }
    .el-dropdown-link {
        cursor: pointer;
        font-size: 16px;
        font-weight: bold;
        color: #FFF;
    }

    .el-icon-arrow-down {
        font-size: 12px;
    }

</style>