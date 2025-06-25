
# 📘 MongoDB Short Notes - Quick Reference

## 🗂️ Basic Concepts
- **MongoDB**: A NoSQL document database that stores data in BSON (Binary JSON) format.
- **Document**: A record in MongoDB (like a row in SQL).
- **Collection**: A group of MongoDB documents (like a table in SQL).
- **Database**: Holds multiple collections.

---

## 🛠️ Core Commands

### 📂 Create / Use Database
```js
use blog
```
> Switches to or creates a database named `blog`.

### 📁 Create Collection
```js
db.createCollection("posts")
```
> Explicitly creates a new collection.

Or via insert:
```js
db.posts.insertOne({...})
```

---

## ✍️ Insert Data

- **Insert one document**:
```js
db.posts.insertOne({ title: "Post", body: "Text" })
```

- **Insert many documents**:
```js
db.posts.insertMany([{...}, {...}])
```

---

## 🔍 Find Data

- **Find all**:
```js
db.posts.find()
```

- **Find one**:
```js
db.posts.findOne()
```

- **Filter query**:
```js
db.posts.find({ category: "News" })
```

- **Projection (select fields)**:
```js
db.posts.find({}, { title: 1, date: 1 })
```

---

## 🔧 Update Data

- **Update one**:
```js
db.posts.updateOne({ title: "Post Title 1" }, { $set: { likes: 2 } })
```

- **Update many**:
```js
db.posts.updateMany({ category: "News" }, { $inc: { likes: 1 } })
```

---

## ❌ Delete Data

- **Delete one**:
```js
db.posts.deleteOne({ title: "Post Title 5" })
```

- **Delete many**:
```js
db.posts.deleteMany({ category: "Technology" })
```

---

## ⚙️ Query Operators

### 📊 Comparison Operators

- **$eq**: Equal to
```js
db.posts.find({ likes: { $eq: 10 } })
```

- **$ne**: Not equal to
```js
db.posts.find({ category: { $ne: "News" } })
```

- **$gt**: Greater than
```js
db.posts.find({ likes: { $gt: 5 } })
```

- **$gte**: Greater than or equal
```js
db.posts.find({ likes: { $gte: 5 } })
```

- **$lt**: Less than
```js
db.posts.find({ likes: { $lt: 5 } })
```

- **$lte**: Less than or equal
```js
db.posts.find({ likes: { $lte: 5 } })
```

- **$in**: In array
```js
db.posts.find({ category: { $in: ["News", "Technology"] } })
```

### 🔗 Logical Operators

- **$and**: Both conditions true
```js
db.posts.find({ $and: [{ category: "News" }, { likes: { $gt: 5 } }] })
```

- **$or**: Either condition true
```js
db.posts.find({ $or: [{ category: "News" }, { likes: { $gt: 5 } }] })
```

- **$nor**: Neither condition true
```js
db.posts.find({ $nor: [{ category: "News" }, { likes: { $gt: 5 } }] })
```

- **$not**: Condition false
```js
db.posts.find({ likes: { $not: { $gt: 5 } } })
```

### 🔍 Evaluation Operators

- **$regex**: Regular expression
```js
db.posts.find({ title: { $regex: "^Post", $options: "i" } })
```

- **$text**: Full-text search (requires text index)
```js
db.posts.find({ $text: { $search: "post" } })
```

- **$where**: JavaScript expression
```js
db.posts.find({ $where: "this.likes > 5" })
```

---

## ⚙️ Shell Tool

- **mongosh**: MongoDB shell to run commands locally.
```bash
mongosh --version
```
