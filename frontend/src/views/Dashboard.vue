```vue
<template>
  <aside class="sidebar">

    <div class="brand">
      <div class="brand-icon">
        ✓
      </div>

      <span>TaskFlow</span>
    </div>

    <nav class="navigation">

      <a href="/dashboard" class="nav-item active">
        <span class="icon">⌂</span>
        Dashboard
      </a>

      <a href="#" class="nav-item">
        <span class="icon">✓</span>
        Tasks
      </a>

      <a href="#" class="nav-item">
        <span class="icon">♙</span>
        Team
      </a>

      <a href="#" class="nav-item">
        <span class="icon">⚙</span>
        Settings
      </a>

    </nav>

    <div class="sidebar-bottom">

      <div class="help-card">
        <div class="help-icon">?</div>

        <div>
          <strong>Need help?</strong>
          <p>Check your workspace</p>
        </div>
      </div>

    </div>

  </aside>
</template>

<style scoped>

.sidebar {
  width: 250px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;

  background: #ffffff;
  border-right: 1px solid #e5e7eb;

  padding: 28px 18px;

  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  align-items: center;
  gap: 11px;

  padding: 0 12px;
  margin-bottom: 45px;

  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.brand-icon {
  width: 34px;
  height: 34px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #18181b;
  color: white;

  border-radius: 9px;

  font-size: 18px;
}

.navigation {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 13px;

  padding: 11px 13px;

  border-radius: 8px;

  color: #71717a;

  font-size: 14px;
  font-weight: 500;

  transition: all 0.2s ease;
}

.nav-item:hover {
  background: #f4f4f5;
  color: #18181b;
}

.nav-item.active {
  background: #f4f4f5;
  color: #18181b;
  font-weight: 600;
}

.icon {
  width: 20px;
  text-align: center;
  font-size: 16px;
}

.sidebar-bottom {
  margin-top: auto;
}

.help-card {
  display: flex;
  align-items: center;
  gap: 10px;

  padding: 13px;

  background: #fafafa;
  border: 1px solid #eeeeee;

  border-radius: 10px;
}

.help-icon {
  width: 28px;
  height: 28px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background: #18181b;
  color: white;

  font-size: 13px;
}

.help-card strong {
  display: block;
  font-size: 12px;
}

.help-card p {
  margin-top: 2px;
  color: #a1a1aa;
  font-size: 10px;
}

</style>
```
