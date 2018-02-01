const Hapi = require('hapi');
const Joi = require('joi');
const Bcrypt = require('bcrypt');
const JWT = require('jsonwebtoken');
const JWT_SECRET_KEY = require('./config')['jwtKey'];
const server = new Hapi.Server();
const Boom = require('boom');

//do second two transaction funcs and all of vendor

// Configure the port on which the server will listen
server.connection({ port: 3000});

const knex = require('knex')({
    client: 'pg',
    connection: {
        host: 'faraday.cse.taylor.edu',
        user: 'davidfletcher',
        password: 'zimoquwo',
        database: 'memex_1_b'
    }
});

const { Model } = require('objection');
Model.knex(knex);

const { UserInfo } = require('./UserInfo');
const { UserProfile } = require('./UserProfile');
const { Transaction } = require('./Transaction');
const { VendorInfo } = require('./VendorInfo');

server.register([
    require('hapi-auth-jwt2')
], err => {
    if(err) { throw err; }

    server.auth.strategy('webtoken', 'jwt', {
        key: JWT_SECRET_KEY,
        validateFunc: validateUser,
        verifyOptions: {algorithms: ['HS256']}
    });

    // Define routes
    server.route([
      {
        method: 'GET',
        path: '/userinfo',
        config: {
            description: 'Fetch all users\' information',
            tags: ['userinfo'],
            notes: ['Every users\' information.'],
            auth: {
                strategy: 'webtoken'
            }
        },
        handler: function(request, reply) {
            UserInfo.query().select()
              .then(data => reply(data))
              .catch(err => reply(err));
        }
      },
      {
        method: 'GET',
        path: '/userinfo/{id}',
        config: {
            description: 'Fetch one user\'s information',
            tags: ['userinfo'],
            notes: ['One user\'s information.'],
            auth: {
                strategy: 'webtoken'
            },
            validate: {
                params: {
                    id: Joi.number().integer().min(1).required().description('Specified user')
                }
            }
        },
        handler: function(request, reply) {
            UserInfo.query()
            .select().where('id',request.params.id )
            .then(result=>reply(result))
            .catch(err => reply(err));
        }
      },
      {
        method: 'PATCH',
        path: '/userinfo/{id}',
        config: {
            description: 'Update one item of a user\'s information',
            tags: ['userinfo'],
            notes: ['One user\'s information.'],
            auth: {
                strategy: 'webtoken'
            },
            validate: {
                params: {
                    id: Joi.number().integer().min(1).required().description('Specified user')
                }
            }
        },
        handler: function(request, reply) {
            if(request.payload.unhashed_password == null) {
                UserInfo.query()
                    .update(request.payload)
                    .where('id', request.params.id)
                    .then(result => reply(result))
                    .catch(err => reply(err));
            } else {
                Bcrypt.hash(request.payload.unhashed_password, 10).then(data => {
                    UserInfo.query().update({ hashed_password: data })
                        .where('id', request.params.id)
                        .then(result => reply(result))
                        .catch(err => reply(err));
                });
            }
        }
      },
      {
        method: 'POST',
        path: '/userinfo',
        config: {
          description: 'Create a new user',
          tags: ['userinfo'],
          notes: ['New user added'],
          validate: {
            payload: {
              login_name: Joi.string().required().description('Username'),
              email_address: Joi.string().email().required().description('Email Address'),
                unhashed_password:Joi.string().min(6).max(32).description('Password'),
        }
          }
        },
        handler: function(request, reply) {
            Bcrypt.hash(request.payload.unhashed_password, 10).then(data => {
                UserInfo.query()
                    .insertGraph([
                        { login_name: request.payload.login_name,
                            email_address: request.payload.email_address,
                            hashed_password: data }
                    ])
                    .then(result=>reply(result))
                    .catch(err => reply(err));
            });
        }
      },
      {
        method: 'GET',
        path: '/userprofile',
        config: {
          description: 'Fetch all users\' profile information',
          tags: ['userprofile'],
          notes: ['All user profile information'],
            auth: {
                strategy: 'webtoken'
            },
        },
        handler: function(request, reply) {
          UserProfile.query().select()
              .then(data => reply(data))
        .catch(err => reply(err));
        }
      },
      {
        method: 'GET',
        path: '/userprofile/{id}',
        config: {
          description: 'Fetch one user\'s profile information',
          tags: ['userprofile'],
          notes: ['One user\'s profile information.'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            params: {
              id: Joi.number().integer().min(1).required().description('Specified user')
            }
          }
        },
        handler: function(request, reply) {
          UserProfile.query().select().where('id', request.params.id)
              .then(data => reply(data))
              .catch(err => reply(err));
        }
      },
      {
        method: 'PATCH',
        path: '/userprofile/{id}',
        config: {
          description: 'Update one user\'s profile information',
          tags: ['userprofile'],
          notes: ['Updated profile information.'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            params: {
              id: Joi.number().integer().min(1).required().description('Specified user')
            },
            payload:{
              full_name: Joi.string().description('First and Last Name'),
              preferred_name: Joi.string().description('Preferred Name'),
              preferred_language: Joi.string().description('Preferred Language'),
              postal_address: Joi.string().description('Address to send mail')
            }
          }
        },
        handler: function(request, reply) {
          UserProfile.query().update(request.payload).select().where('id', request.params.id)
              .then(data => reply(data))
              .catch(err => reply(err));
        }
      },
      {
        method: 'POST',
        path: '/userprofile',
        config: {
          description: 'Create a new user profile',
          tags: ['userprofile'],
          notes: ['New user profile created'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            payload: {
              full_name: Joi.string().required().description('First and Last Name'),
              preferred_name: Joi.string().required().description('Preferred Name'),
              preferred_language: Joi.string().required().description('Preferred Language'),
              postal_address: Joi.string().required().description('Address to send mail')
            }
          }
        },
        handler: function(request, reply) {
          UserProfile.query().insert(request.payload)
              .then(data => reply(data))
              .catch(err => reply(err));
        }
      },
      {
        method: 'GET',
        path: '/vendor',
        config: {
          description: 'Fetch all vendors\' information',
          tags: ['vendor'],
          notes: ['All vendor information'],
            auth: {
                strategy: 'webtoken'
            },
        },
        handler: function(request, reply) {
            VendorInfo.query().select()
                .then(data => reply(data))
                .catch(err => reply(err));
        }
      },
      {
        method: 'GET',
        path: '/vendor/{id}',
        config: {
          description: 'Fetch one vendor\'s information',
          tags: ['vendor'],
          notes: ['One vendor\'s information'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            params: {
              id: Joi.number().integer().min(1).required().description('Specified user')
            }
          }
        },
        handler: function(request, reply) {
            vendorId = request.params.id;
            VendorInfo.query().select().where('id',vendorId)
                .then(data => {
                    reply(data);
                })
                .catch(err => reply(err));
        }
      },
      {
        method: 'PATCH',
        path: '/vendor/{id}',
        config: {
          description: 'Update one vendor\'s information',
          tags: ['vendor'],
          notes: ['Updated vendor information'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            params: {
              id: Joi.number().integer().min(1).required().description('Specified user')
            }
          }
        },
        handler: function(request, reply) {
            vendorId = request.params.id;
            VendorInfo.query().update(request.payload)
            .where('id',vendorId)
            .then(rowsUpdated => reply({ updated: rowsUpdated}))
            .catch(err => reply(err));
        }
      },
      {
        method: 'POST',
        path: '/vendor',
        config: {
          description: 'Create a new vendor',
          tags: ['vendor'],
          notes: ['New vendor created'],
          validate: {
            payload: {
              vendor_name: Joi.string().required().description('Vendor Name')
            }
          }
        },
        handler: function(request, reply) {
            VendorInfo.query().insert(request.payload)
            .then(vendor => reply(vendor))
            .catch(err => reply(err));
        }
      },
      {
        method: 'GET',
        path: '/transaction',
        config: {
          description: 'Fetch every transaction\'s information',
          tags: ['transaction'],
          notes: ['All transaction information'],
            auth: {
                strategy: 'webtoken'
            },
        },
        handler: function(request, reply) {
          Transaction.query().select()
              .then(data => reply(data))
              .catch(err => reply(err));

        }
      },
      {
        method: 'GET',
        path: '/transaction/{id}',
        config: {
          description: 'Fetch one user\'s information for this transaction',
          tags: ['transaction'],
          notes: ['One user\'s transaction information'],
            auth: {
                strategy: 'webtoken'
            },
          validate: {
            params: {
              id: Joi.number().integer().min(1).required().description('Specified user')
            }
          }
        },
        handler: function(request, reply) {
          Transaction.query()
            .select().where('id', request.params.id)
            .then(result=>reply(result))
            .catch(err => reply(err))

        }
      },
      {
        method: 'POST',
        path: '/transaction',
        config: {
          description: 'Create a new transaction',
          tags: ['transaction'],
          notes: ['New transaction created'],
          validate: {
            payload: {
              user_info_id: Joi.number().integer().min(1).required().description('user info id'),
              vendor_info_id: Joi.number().integer().min(1).required().description('vendor info id'),
              date: Joi.string().required().description('Date of transaction'),
              price: Joi.number().min(0).required().description('Price of transaction')
            }
          }
        },
        handler: function(request, reply) {
          Transaction.query().insert(request.payload)
              .then(data => reply(data))
              .catch(err => reply(err));
        }
      },
        {
          method: 'DELETE',
          path: '/transaction/{id}',
          config: {
            description: 'Delete one user\'s profile information',
            tags: ['userprofile'],
            notes: ['Delete Information'],
              auth: {
                  strategy: 'webtoken'
              },
          },
          handler: function(request, reply) {
            Transaction.query().delete().where('id', request.params.id)
                .then(data => reply(data))
                .catch(err => reply(err));
          }
        },
        {
            method: 'POST',
            path: '/authenticate',
            config: {
                description: 'Authenticate a user',
                tags: ['authentication'],
                notes: ['Log in a user'],
                validate: {
                    payload: {
                        email_address: Joi.string().email().required().description('User\'s email address'),
                        password: Joi.string().min(6).max(32).required().description('User\'s password')
                    }
                }
            },
            handler: function(request, reply) {
                let userId = null;
                let authenticated = false;
                UserInfo.query()
                    .where('email_address', request.payload.email_address)
                    .first()
                    .then(user => {
                        if(!user) {
                            authenticated = false;
                        } else {
                            userId = user.id;
                        }

                        return new Promise((resolve, reject) => {
                            resolve(user); // we always resolve so that the server takes the same amount of time to process valid and invalid requests -- this prevents hackers from doing timed attacks to learn info
                        });
                    })
                    .then((user) => {
                        if(user) {
                            return Bcrypt.compare(request.payload.password, user.hashed_password);
                        } else {
                            return new Promise((resolve, reject) => {
                                resolve(false);
                            });
                        }
                    })
                    .then((isValid) => {
                        return new Promise((resolve, reject) => {
                            if(isValid) {
                                resolve();
                            } else {
                                reject();
                            }
                        })
                    })
                    .then(() => {
                        reply({ jwtIdToken: createToken(userId)})
                    })
                    .catch(() => {
                        reply(Boom.unauthorized('Email or password are not correct'))
                    });
            }
        }


    ]);

    server.register([
        require('vision'),
        require('inert'),
        require('lout'),
    ], err => {
        if (err) {
            throw err;
        }

        server.start(err => {
            if (err) {
                throw err;
            }
            console.log('Server running at', server.info.uri);
        });
    });
});


function createToken(userId) {
    return JWT.sign(
        {userId: userId},
        JWT_SECRET_KEY,
        {algorithm: 'HS256', expiresIn: '1d'}
    );
}

function validateUser(decoded, request, callback) {
    if(decoded.hasOwnProperty('userId')) {
        UserInfo.query().findById(decoded.userId)
            .then(user => {
                if (user) {
                    callback(null, true);
                } else {
                    callback(null, false);
                }
            })
            .catch(err => callback(err, false));
    } else {
        callback(null, false);
    }
}

module.exports = server
