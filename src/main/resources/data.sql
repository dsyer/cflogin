insert into oauth_client_details
	(client_id,client_secret,scope,authorized_grant_types,autoapprove)
	values ('cf', '', 'openid,cloud_controller.read,cloud_controller.write', 'password,authorization_code,refresh_token', 'true');
insert into user_client_details
	(client_id,username)
	values ('cf', 'dsyer@gopivotal.com');