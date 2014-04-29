exports.config =
  # See docs at http://brunch.readthedocs.org/en/latest/config.html.
  modules:
    definition: false
    wrapper: false
  paths:
    public: 'public'
  files:
    javascripts:
      joinTo:
        'javascripts/app.js':    /^app/
        'javascripts/vendor.js':  /^(bower_components|vendor)/
    stylesheets:
      joinTo:
        'stylesheets/app.css': /^(app|vendor)/
  #Since Play compiles app/assets files to resource_managed under target, you don't want Brunch to copy app/assets files to public
  conventions:
    assets: /^brunch-assets/