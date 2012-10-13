idea

"""select
  idea.id               as idea_id,
  
    idea_type.id             as idea_type_id,
    idea_type.name           as idea_type_name,
    idea_type.description    as idea_type_description,
  
  idea.name             as idea_name,
  idea.description      as idea_description,

    idea_author.id           as idea_author_id,
    idea_author.nickname     as idea_author_nickname,
    idea_author.name         as idea_author_name,
    idea_author.email        as idea_author_email,
    idea_author.avatar       as idea_author_avatar, 
    idea_author.created      as idea_author_created,

  idea.views            as idea_views,
  idea.created          as idea_created
from 
  idea                                                                  inner join 
  idea_type as idea_type      on idea.idea_type_id  = idea_type.id      inner join 
  user      as idea_author    on idea.user_id       = idea_author.id""".stripMargin

comment

"""select

  comment.id            as comment_id,

    idea.id               as comment_idea_id,

      idea_type.id           as comment_idea_type_id,
      idea_type.name         as comment_idea_type_name,
      idea_type.description  as comment_idea_type_description,
    
    comment_idea.name             as comment_idea_name,
    comment_idea.description      as comment_idea_description,

      comment_idea_author.id            as comment_idea_author_id,
      comment_idea_author.nickname      as comment_idea_author_nickname,
      comment_idea_author.name          as comment_idea_author_name,
      comment_idea_author.email         as comment_idea_author_email,
      comment_idea_author.avatar        as comment_idea_author_avatar, 
      comment_idea_author.created       as comment_idea_author_created,

    comment_idea.views            as comment_idea_views,
    comment_idea.created          as comment_idea_created

    comment_author.id            as comment_author_id,
    comment_author.nickname      as comment_author_nickname,
    comment_author.name          as comment_author_name,
    comment_author.email         as comment_author_email,
    comment_author.avatar        as comment_author_avatar, 
    comment_author.created       as comment_author_created,

from 
  comment                                                                                   inner join
  idea          as comment_idea           on comment.idea_id    = comment_idea.id           inner join
    idea_type   as comment_idea_type      on idea.idea_type_id  = comment_idea_type.id      inner join 
    user        as comment_idea_author    on idea.user_id       = comment_idea_author.id    inner join
  user          as comment_author         on comment.user_id    = comment_author.id
""".stripMargin

    

user

"""select
  user.id            as user_id,
  user.nickname      as user_nickname,
  user.name          as user_name,
  user.email         as user_email,
  user.avatar        as user_avatar, 
  user.created       as user_created,
from 
  user as user

--

select idea.*, idea_type.*, user.*
from
idea                                               inner join 
idea_type  on idea.idea_type_id = idea_type.id     inner join 
user as author      on idea.user_id = user.id


select idea.*, author.* from idea inner join user as author on idea.user_id = author.id

select idea.*, author.*, author2.* 
from idea 
inner join user as author on idea.user_id = author.id
inner join user as author2 on idea.idea_type_id = author2.id