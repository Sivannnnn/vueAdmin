<template>
    <el-row type="flex" class="row-bg" justify="center">
        <el-col :xl="6" :lg="7">
            <h2>欢迎来到VueAdmin管理系统</h2>
            <el-image :src="require('@/assets/logo.png')" style="width: 180px;height: 180px"></el-image>
        </el-col>
        <el-col :span="1">
            <el-divider direction="vertical"></el-divider>
        </el-col>
        <el-col :xl="6" :lg="7">
            <el-form :model="loginFrom" :rules="rules" ref="loginFrom" label-width="80px" class="demo-loginFrom">
                <el-form-item label="用户名" prop="username" style="width: 380px">
                    <el-input v-model="loginFrom.username"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password" style="width: 380px">
                    <el-input v-model="loginFrom.password" type="password"></el-input>
                </el-form-item>
                <el-form-item label="验证码" prop="code" style="width: 380px">
                    <el-input v-model="loginFrom.code" style="width: 172px;float: left" maxlength="5"></el-input>
                    <el-image :src="captchaImg" class="captchImg" @click="getCaptcha"></el-image>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginFrom')">立即创建</el-button>
                    <el-button @click="resetForm('loginFrom')">重置</el-button>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<script>
    import qs from 'qs'
    export default {
        name: "Login",
        data() {
            return {
                loginFrom: {
                    username: '',
                    password: '',
                    code: '',
                    token: ''
                },
                rules: {
                    username: [
                        { required: true, message: '请输入用户名', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' }
                    ],
                    code: [
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                        { min: 5, max: 5, message: '长度为 5 个字符', trigger: 'blur' }
                    ]
                },
                captchaImg: null
            };
        },
        methods: {
            // 登录 提交表单
            submitForm(formName) {
                // 开始验证
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        console.log(this.loginFrom)
                        this.$axios.post('/login',qs.stringify(this.loginFrom),
                            {
                                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                            })
                            .then(res => {
                                const jwt = res.headers['authorization']    // 请求头
                                this.$store.commit('SET_TOKEN',jwt)     // 产生TOKEN信息
                                this.$router.push("/index")     // 跳转到index页面
                        })
                    } else {
                        // console.log('error submit!!');
                        return false;
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            getCaptcha(){
                this.$axios.get('/captcha').then(res => {

                    // console.log('/captcha')
                    // console.log(res)

                    this.loginFrom.token = res.data.data.token
                    this.captchaImg = res.data.data.captchaImg
                    // 清空验证码输入的内容
                    this.loginFrom.code = '';
                    //Result - code、msg、data
                })
            }
        },
        created(){
            this.getCaptcha()
        }
    }
</script>

<style scoped>
    .el-row{
        background-color: #fafafa;
        height: 100%;
        display: flex;
        align-items: center;
        text-align: center;
    }
    .el-divider{
        height: 200px;
    }
    .captchImg{
        float: left;
        margin-left: 8px;
        border-radius: 4px;
    }
</style>