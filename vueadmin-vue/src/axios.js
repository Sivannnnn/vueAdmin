import axios from "axios";
import router from "./router";  //当出现没有权限的时候，就要跳到登录页面去
import Element from "element-ui" //显示弹窗

axios.defaults.baseURL = "http://localhost:8081"

const request = axios.create({
    timeout: 5000,
    headers:{
        'Content-Type': "application/json; charset=utf-8",
        // 'Content-Type': "application/x-www-form-urlencoded"
    }
})



//对请求结果的拦截
request.interceptors.request.use(config =>{
    //请求结果在headers添加Authorization属性，其值为本地缓存名为token的值
    config.headers['Authorization'] = localStorage.getItem("token")
    return config
})

//对请求结果的拦截
request.interceptors.response.use(
    response => {
        console.log("response ->",response)
        let res = response.data

        if(res.code === 200){
            return response //返回响应信息
        }else{
            Element.Message.error(res.msg? res.msg :'系统异常')   //弹窗提示错误信息
            return Promise.reject(response.data.msg)    //不让代码继续执行
        }
    },error => {    //出现异常的情况处理，如：断言、用户权限问题等等

        if(error.response.data) {   //判断是否有响应数据是否有异常信息
            error.message = error.response.data.msg
        }

        if(error.response.status === 401) { // 用户没有权限的情况
            router.push("/login")   //跳回登陆页面
        }
        Element.Message.error(error.message,{duration:3000})    //打印系统异常

        return Promise.reject(error)
    }
)

export default request //暴露出去