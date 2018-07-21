const express = require('express')
const path = require('path')
const app = express()

app.get('/_ping', (req, res) => res.send('pong!'))
app.use('/', express.static(path.join(__dirname, '/')))

app.listen(3000, () => console.log('TD app listening on port 3000!'))