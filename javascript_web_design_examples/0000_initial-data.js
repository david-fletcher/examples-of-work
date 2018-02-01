
exports.seed = function(knex, Promise) {

  const { Model } = require('objection');
  Model.knex(knex);

  const { UserInfo } = require('./../UserInfo');
  const { UserProfile } = require('./../UserProfile');
  const { Transaction } = require('./../Transaction');
  const { VendorInfo } = require('./../VendorInfo');

  return knex('user_info_user_profile').del()
    .then(() => knex('transaction').del())
    .then(() => knex('user_profile').del())
    .then(() => knex('vendor_info').del())
    .then(() => knex('user_info').del())
    .then(() => UserInfo.query().insertGraph([
        {
            "#id": 'info',
            id: 1,
            login_name: 'johndoe',
            email_address: 'johndoe@example.com',
            hashed_password: '$2a$10$5oh32vYiXdhZWWhfdd1cv.rCV.HJT0oIUCG6GT2IkjHywe8aGHc9G',
            vendorinfo: [
		        {
		            "#id": 'chuckcheese',
                    id: 1,
			        vendor_name: 'Chuck E. Cheese'
                },
                // FIXME: --- not sure why the below breaks the code ---
                // {
                //     "#id": 'daveandbuster',
                //     vendor_name: 'Dave and Buster'
                // }
		    ],
            transaction: [
		        {
		            id: 1,
			        date: '2017-12-2',
			        price: '4.99',
			        vendor_info_id: '#ref{chuckcheese.id}'
                },
                // FIXME: --- not sure why the below breaks the code ---
                // {
                //     date: '2017-12-3',
                //     price: '2.99',
                //     vendor_info_id: '#ref{daveandbuster.id}'
                // }
		    ],
            userprofile: [
                {
                    "#id": 'profile',
                    id: 1,
                    full_name: 'John Doe',
                    preferred_name: 'Johnny Doe',
                    preferred_language: 'English',
                    postal_address: '1234 Example Street, Some Hill CA, 78421'
                }
            ],
            userinfouserprofile: [
                {
                    id: 1,
                    user_info_id: "#ref{info.id}",
                    user_profile_id: "#ref{profile.id}"
                }
            ]
        },
        {
            "#id": 'info2',
            id: 2,
            login_name: 'tomnurk',
            email_address: 'tomnurk@taylor.edu',
            hashed_password: '$2a$10$.Fz.gq81FRn7XadxYeYjau3TpoyjanP6rQwwlH0DXGVIGv0HXBV1K',
            vendorinfo: [
                {
                    "#id": 'quiznos',
                    id: 2,
                    vendor_name: 'Quiznos'
                }
            ],
            transaction: [
                {
                    id: 2,
                    date: '2015-03-12',
                    price: '8.99',
                    vendor_info_id: '#ref{quiznos.id}'
                }
            ],
            userprofile: [
                {
                    "#id": 'profile2',
                    id: 2,
                    full_name: 'Tom Nurkkala',
                    preferred_name: 'The Nurk',
                    preferred_language: 'Javascript',
                    postal_address: '456 8th Street, Upland IN, 46989'
                }
            ],
            userinfouserprofile: [
                {
                    id: 2,
                    user_info_id: "#ref{info2.id}",
                    user_profile_id: "#ref{profile2.id}"
                }
            ]
        },
        {
            "#id": 'info3',
            id: 3,
            login_name: 'dfletch',
            email_address: 'gregariousgerigian@lester.com',
            hashed_password: '$2a$10$r0pR1anv/tOl7SsI6hXFO./2yXwrDyR41kMDDhA36VY36lfxqS0M.',
            vendorinfo: [
                {
                    "#id": 'tayloruniv',
                    id: 3,
                    vendor_name: 'Taylor University'
                }
            ],
            transaction: [
                {
                    id: 3,
                    date: '2005-09-12',
                    price: '2.99',
                    vendor_info_id: '#ref{tayloruniv.id}'
                }
            ],
            userprofile: [
                {
                    "#id": 'profile3',
                    id: 3,
                    full_name: 'David Fletcher',
                    preferred_name: 'Fletch',
                    preferred_language: 'Nyanja',
                    postal_address: '18301 Yellow Schoolhouse Road, Round Hill VA, 20141'
                }
            ],
            userinfouserprofile: [
                {
                    id: 3,
                    user_info_id: "#ref{info3.id}",
                    user_profile_id: "#ref{profile3.id}"
                }
            ]
        }
    ]));
};
