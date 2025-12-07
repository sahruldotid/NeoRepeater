# NeoRepeater

A Burp Suite extension that enhances the Repeater workflow with tab naming from the HTTP request.

## Features

- **Smart Tab Naming**: Automatically generates clean, readable tab names in the format `N. [METHOD] /full/path`
- **Keyboard Shortcut**: Send requests to Repeater with `Ctrl+R` from any HTTP message editor
- **Persistent Counter**: Tab numbers persist across Burp sessions within the same project
- **Path Truncation**: Automatically truncates long paths to 25 characters with ellipsis on both ends

## Installation

1. Download the compiled JAR file or build from source
2. In Burp Suite, go to **Extensions** → **Installed**
3. Click **Add**
4. Select **Extension type**: Java
5. Select the NeoRepeater JAR file
6. Click **Next**

## Usage

### Sending Requests to Repeater

1. Unbind the default Burp shortcut for sending to Repeater
2. Open any HTTP request in Burp (Proxy history, Intruder, Site map, etc.)
3. Press `Ctrl+R` to send the request to Repeater
4. The request will appear in a new Repeater tab with a smart name

### Tab Naming Format

Tabs are automatically named in the format: `N. [METHOD] /full/path`

**Examples with short paths:**
- `1. [GET] /api/users`
- `2. [POST] /auth/login`
- `3. [DELETE] /products`
- `4. [PUT] /api/v1/settings`
- `5. [GET] /api/v1/users/123`

**Examples with long paths (>25 characters):**
- `/api/v1/users/123/profile/settings` → `6. [GET] .../profile/settings...`
- `/authentication/validate/token/refresh` → `7. [POST] .../token/refresh...`
- `/very/long/endpoint/path/structure` → `8. [DELETE] ...path/structure...`

### Path Display Rules

- **Paths ≤ 25 characters**: Displayed in full
  - `/api/v1/users` → `/api/v1/users`
  - `/auth/login` → `/auth/login`
  - `/api/v1/products/search` → `/api/v1/products/search`
  
- **Paths > 25 characters**: Truncated with ellipsis on both ends
  - `/api/v1/users/123/profile/settings` → `.../profile/settings...`
  - `/authentication/validate/token/refresh` → `.../token/refresh...`

The extension intelligently shows the end portion of long paths while maintaining a consistent 25-character limit, ensuring you can still identify the endpoint.


## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.


## Changelog

### v1.0.0 (Current)
- Initial release
- Persistent counter per project
- Keyboard shortcuts
- Project-based persistence using Burp's storage API

