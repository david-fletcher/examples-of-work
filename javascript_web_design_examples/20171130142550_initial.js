
exports.up = function(knex, Promise) {
  return knex.schema.createTable('user_info', table => {
    table.increments();
    table.string('login_name');
	table.string('email_address');
  	table.string('hashed_password');
  })
    .then(() => knex.schema.createTable('user_profile', table => {
      table.increments();
      table.string('full_name');
      table.string('preferred_name');
      table.string('preferred_language');
      table.string('postal_address');
	}))
	.then(() => knex.schema.createTable('user_info_user_profile', table => {
	  table.increments();
	  table.integer('user_info_id').references('user_info.id');
	  table.integer('user_profile_id').references('user_profile.id');
    }))
    .then(() => knex.schema.createTable('vendor_info', table => {
      table.increments();
      table.string('vendor_name');
    }))
    .then(() => knex.schema.createTable('transaction', table => {
	  table.increments();
      table.integer('user_info_id').references('user_info.id');
      table.integer('vendor_info_id').references('vendor_info.id');
      table.date('date');
      table.decimal('price');
    }));
};

exports.down = function(knex, Promise) {
  return knex.schema.dropTable('transaction')
    .then(() => knex.schema.dropTable('user_info_user_profile'))
    .then(() => knex.schema.dropTable('vendor_info'))
    .then(() => knex.schema.dropTable('user_profile'))
    .then(() => knex.schema.dropTable('user_info'));
};
