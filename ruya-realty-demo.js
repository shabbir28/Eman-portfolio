(() => {
  const screen = document.getElementById('ruya-screen');
  if (!screen) return;
  const button = (label, target) => `<button type="button" data-ruya="${target}">${label}</button>`;
  const screens = {
    welcome: ['Welcome', `<div class="ruya-welcome"><div><img src="../assets/ruya-realty/logo.png" alt="Ruya Realty"><h3>WELCOME</h3><p>Find your next space, feel at home</p><p>Where comfort meets convenience</p>${button('Start exploring', 'find')}</div></div>`],
    find: ['Find a home · Onboarding 1 of 2', `<div class="ruya-onboard">${button('Skip', 'account')}<h3>Find your perfect home!</h3><p>Filter by furnishing, location, type and price &amp; reach the seller directly!</p><span aria-hidden="true">━ ·</span><img src="../assets/ruya-realty/home.jpg" alt="Modern home inspiration">${button('Next', 'list')}</div>`],
    list: ['List a property · Onboarding 2 of 2', `<div class="ruya-onboard">${button('Skip', 'account')}<h3>List your property!</h3><p>Get verified buyers of your property with an easy to manage dashboard!</p><span aria-hidden="true">· ━</span><img src="../assets/ruya-realty/property.jpg" alt="Property inspiration">${button('Get started', 'account')}</div>`],
    account: ['Account entry · Visual preview', `<div class="ruya-account"><div class="ruya-brand">RUYA REALTY</div><h3>A space to call yours.</h3><p>Explore the account interface.</p>${button('Login', 'login')}${button('Sign up preview', 'signup')}${button('Back to welcome', 'welcome')}</div>`],
    login: ['Login · Visual preview only', `<div class="ruya-account"><div class="ruya-brand">RUYA REALTY</div><h3>Login</h3><label>Email address<input type="email" placeholder="Email Address" disabled></label><label>Password<input type="password" placeholder="Password" disabled></label>${button('Forgot password preview', 'reset')}<p class="ruya-notice">UI preview only. Login is not connected.</p>${button('Back to account', 'account')}</div>`],
    signup: ['Sign up · Visual preview only', `<div class="ruya-account"><div class="ruya-brand">RUYA REALTY</div><h3>Create an account</h3><label>Name<input placeholder="Full name" disabled></label><label>Email address<input placeholder="Email Address" disabled></label><label>Password<input placeholder="Password" disabled></label><p class="ruya-notice">UI preview only. Registration is not connected.</p>${button('Back to account', 'account')}</div>`],
    reset: ['Password recovery · Visual preview only', `<div class="ruya-account"><div class="ruya-brand">RUYA REALTY</div><h3>Forgot password?</h3><label>Email address<input placeholder="Email Address" disabled></label><p class="ruya-notice">UI preview only. No recovery email will be sent.</p>${button('Back to login', 'login')}</div>`]
  };
  function show(name, moveFocus = false) {
    if (!screens[name]) return;
    screen.innerHTML = screens[name][1];
    document.getElementById('ruya-caption').textContent = screens[name][0];
    document.querySelectorAll('.ruya-controls button').forEach(control => control.setAttribute('aria-pressed', String(control.dataset.ruya === name)));
    if (moveFocus) {
      const heading = screen.querySelector('h3');
      heading.tabIndex = -1;
      heading.focus({ preventScroll: true });
    }
  }
  document.querySelector('.ruya-demo').addEventListener('click', event => {
    const control = event.target.closest('button[data-ruya]');
    if (control) show(control.dataset.ruya, screen.contains(control));
  });
  show('welcome');
})();
