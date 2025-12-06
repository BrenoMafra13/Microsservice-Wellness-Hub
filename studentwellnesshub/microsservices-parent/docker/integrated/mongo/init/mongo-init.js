db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE || 'goaldb');

db.createUser({
  user: process.env.MONGO_APP_USER || 'appuser',
  pwd:  process.env.MONGO_APP_PASS || 'apppass',
  roles: [{ role: 'readWrite', db: db.getName() }]
});

db.createCollection('goals');