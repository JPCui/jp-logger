# 数据库初始化

```
// 创建角色
db.createRole(
    {
        role: "admin",
        privileges: [{
            resource: { db: "<database>", collection: "" },
            actions: [ "find", "update", "insert", "remove" ]
        }],
        roles: []
    },
    {
        w: "majority" , wtimeout: 5000
    }
)

// 创建用户
db.createUser(
    {
        user: "<user>",
        pwd: "<password>",
        roles: [{
            role: "admin",
            db: "<database>"
        }]
    }
)

// 验证权限
db.auth(
    {
        user: "<user>",
        pwd: "<password>"
    }
)
```


