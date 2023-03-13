<template>
    <div>
        <el-form :inline="true">
            <el-form-item>
                <el-button type="primary" @click="dialogVisible = true">新增</el-button>
            </el-form-item>
        </el-form>
        <el-table
                :data="tableData"
                style="width: 100%;margin-bottom: 20px;"
                row-key="id"
                border
                stripe
                default-expand-all
                :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
            <el-table-column
                    prop="name"
                    label="名称"
                    sortable
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="perms"
                    label="权限编码"
                    sortable
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="icon"
                    label="图标">
            </el-table-column>
            <el-table-column
                    prop="type"
                    label="类型">
                <template slot-scope="scope">
                    <el-tag size="small" v-if="scope.row.type === 0">目录</el-tag>
                    <el-tag size="small" v-else-if="scope.row.type === 1" type="success">菜单</el-tag>
                    <el-tag size="small" v-else-if="scope.row.type === 2" type="info">按钮</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                    prop="path"
                    label="菜单URL">
            </el-table-column>
            <el-table-column
                    prop="component"
                    label="菜单组件">
            </el-table-column>
            <el-table-column
                    prop="orderNum"
                    label="排序号">
            </el-table-column>
            <el-table-column
                    prop="statu"
                    label="状态">
                <template slot-scope="scope">
                    <el-tag size="small" v-if="scope.row.statu === 1" type="success">正常</el-tag>
                    <el-tag size="small" v-else-if="scope.row.statu === 0" type="danger">禁用</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                    prop="orderNum"
                    label="操作">
                <template slot-scope="scope">
                    <el-button type="text" @click="editHandle(scope.row.id)">编辑</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <template>
                        <el-popconfirm title="这是一段内容确定删除吗？" @confirm="delHandle(scope.row.id)">
                            <el-button slot="reference" type="text">删除</el-button>
                        </el-popconfirm>
                    </template>
                </template>
            </el-table-column>
        </el-table>
        <!--新增对话框-->
        <el-dialog
                title="提示"
                :visible.sync="dialogVisible"
                width="600px"
                :before-close="handleClose">
            <el-form :model="editForm" :rules="editFormRules" ref="editForm">
                <el-form-item label="上级菜单" prop="parentId" label-width="100px">
                    <!--模拟树形下拉框-->
                    <el-select v-model="editForm.parentId" placeholder="请选择上级菜单" >
                        <template v-for="item in tableData">
                            <el-option :label="item.name" :value="item.id"></el-option>
                            <template v-for="child in item.children">
                                <el-option :label="child.name" :value="child.id">
                                    <span>{{ '- ' + child.name }}</span>
                                </el-option>
                            </template>
                        </template>
                    </el-select>
                </el-form-item>
                <el-form-item label="菜单名称" prop="name" label-width="100px">
                    <el-input v-model="editForm.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="权限编码" prop="perms" label-width="100px">
                    <el-input v-model="editForm.perms" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="图标" prop="icon" label-width="100px">
                    <el-input v-model="editForm.icon" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="菜单URL" prop="path" label-width="100px">
                    <el-input v-model="editForm.path" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="菜单组件" prop="component" label-width="100px">
                    <el-input v-model="editForm.component" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="类型" prop="type" label-width="100px">
                    <el-radio-group v-model="editForm.type">
                        <el-radio :label=0>目录</el-radio>
                        <el-radio :label=1>菜单</el-radio>
                        <el-radio :label=2>按钮</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="状态" prop="statu" label-width="100px">
                    <el-radio-group v-model="editForm.statu">
                        <el-radio :label=0>禁用</el-radio>
                        <el-radio :label=1>正常</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="排序号" prop="orderNum" label-width="100px">
                    <el-input-number v-model="editForm.orderNum" :min="1" label="排序号">1</el-input-number>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="resetForm('editForm')">取 消</el-button>
                <el-button type="primary" @click="submitEditForm('editForm')">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Menu",
        data(){
            return {
                // 编辑表单
                editForm:{},
                // 表单小队规则
                editFormRules: {
                    parentId: [
                        {required: true, message: '请选择上级菜单', trigger: 'blur'}
                    ],
                        name: [
                        {required: true, message: '请输入名称', trigger: 'blur'}
                    ],
                        perms: [
                        {required: true, message: '请输入权限编码', trigger: 'blur'}
                    ],
                        type: [
                        {required: true, message: '请选择状态', trigger: 'blur'}
                    ],
                        orderNum: [
                        {required: true, message: '请填入排序号', trigger: 'blur'}
                    ],
                        statu: [
                        {required: true, message: '请选择状态', trigger: 'blur'}
                    ]
                },
                tableData: [],  // 菜单数据
                dialogVisible: false    // 弹窗
            }
        },
        // 获取菜单数据
        created() {
            this.getMenuTree()
        },
        methods: {
            // 获取菜单列表
            getMenuTree(){
                // 发送get请求
                this.$axios.get('/sys/menu/list').then(res => {
                    // 将返回值赋给table数据
                    this.tableData = res.data.data
                })
            },
            // 立即创建方法
            submitEditForm(formName){
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        // 为了减少代码段，实现路径判断公用
                        // 通过 this.editForm.id 是否存在判断
                        // 如果存在，那么发送post请求 更新（update），否则发送 创建保存（save）
                        this.$axios.post('/sys/menu/' + (this.editForm.id? 'update' : 'save'),this.editForm)
                            .then(res => {
                                // 请求成功，弹窗提示信息
                                this.$message({
                                    showClose: true,
                                    message: '恭喜你，操作成功！',
                                    type: 'success',
                                    // 弹窗关闭后，调用获取菜单信息
                                    onClose: () => {
                                        this.getMenuTree()
                                    }
                                });
                                // 关闭弹窗
                                this.dialogVisible = false
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            // 点击编辑并打开编辑弹窗
            editHandle(id){
                // 根据传过来的id发送post请求，获取当前数据信息
                this.$axios.get('/sys/menu/info/' + id).then(res => {
                    // 将返回值赋给编辑表单
                    this.editForm = res.data.data;
                    // 弹窗显示
                    this.dialogVisible = true
                })
            },
            // 重置
            resetForm(formName){
                this.$refs[formName].resetFields();
                // 关闭编辑弹窗
                this.dialogVisible = false
                // 重置编辑表单
                this.editForm = {}
            },
            // 关闭对话框
            handleClose(){
                this.editHandle('editForm')
            },
            // 删除
            delHandle(id){
                // 发送post请求，删除对应的id
                this.$axios.post('/sys/menu/delete/' + id).then(res => {
                    // 请求成功 消息弹出成功提示
                    this.$message({
                        showClose: true,
                        message: '恭喜你，操作成功！',
                        type: 'success',
                        onClose: () => {
                            // 重新获取菜单信息
                            this.getMenuTree()
                        }
                    });
                })
            }
        }
    }
</script>