use serde_json::json;
use log::info;
mod java_glue;
pub use crate::java_glue::*;

mod shared;
use shared::{DelayEvent, Delay, ExecuteJsData};

struct PokerBros {}

impl PokerBros {
    pub fn new() -> PokerBros {
        PokerBros {}
    }

    pub fn login(username: &str, password: &str) -> String {
        let data = ExecuteJsData::new("networkController".to_string(), "gate.gateHandler.login".to_string(), json!({
            "snsName": username,
            "snsToken": password,
            "snsType": 0,
        }));

        let event = DelayEvent::new(data, Delay::new());

        serde_json::to_string(&event).unwrap()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn pokerbros_login() {
        let event = PokerBros::login("test", "test");

        assert!(event == "{\"data\":{\"source_code\":\"cc.js.getClassByName('networkController').getInstance().request('gate.gateHandler.login', {\\\"snsName\\\":\\\"test\\\",\\\"snsToken\\\":\\\"test\\\",\\\"snsType\\\":0}, function(e) networkController);\",\"type\":\"executeJS\"},\"delay\":{\"before\":1000,\"after\":1000}}", "serialized = {}", event);
    }
}