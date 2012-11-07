host = "ideasba.org"
set :application, "ideasBA"
set :repository,  "git@github.com:RestOpenGov/ideas-ba.git"
set :keep_releases, 10 

set :scm, :git
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

#role :web, "zoxial.com"                          # Your HTTP server, Apache/etc
role :app, host                          # This may be the same as your `Web` server
#role :db,  "zoxial.com", :primary => true # This is where Rails migrations will run
#role :db,  "zoxial.com"

server host, :app, :web, :db, :primary => true

default_environment['PATH'] = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/home/ubuntu/dev/jdk/bin:/home/ubuntu/dev/play"

set :deploy_to, "/var/www/ideasBA" 
set :use_sudo, false

set :ssh_options, {:forward_agent => true}
set :user, 'ubuntu'
default_run_options[:pty] = false 

set :git_shallow_clone, 1

server_path = "#{current_path}/webservice"

namespace :deploy do

  desc "Start #{application}"
  task :start do
    run "cd #{server_path};" +
    'play start;'
  end

  desc "Stop #{application}"
  task :stop do
  run "cd #{server_path};" +
    'play stop;'
  end
  
  desc "Restart #{application}"
  task :restart do
   stop
   start
  end

end


# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end