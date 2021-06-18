use rand::Rng;
use serde::Serialize;
use serde_json::Value;

const TICK_DELAY: i32 = 1000;

pub fn create_delay_between(min: i32, max: i32) -> i32 {
    let mut rng = rand::thread_rng();

    rng.gen_range(min..max) * TICK_DELAY
}

#[derive(Serialize)]
pub struct Delay {
    before: i32,
    after: i32,
}

impl Delay {
    pub fn new() -> Self {
        Delay { before: create_delay_between(1, 2), after: create_delay_between(1, 2) }
    }

    fn before_between(mut self, min: i32, max: i32) -> Self {
        self.before = create_delay_between(min, max);
        self
    }

    fn after_between(mut self, min: i32, max: i32) -> Self {
        self.before = create_delay_between(min, max);
        self
    }
}

#[derive(Serialize)]
pub struct ExecuteJsData {
    source_code: String,
    r#type: String
}

impl ExecuteJsData {
    pub fn new(class_name: String, event_name: String, body: serde_json::Value) -> ExecuteJsData {
        ExecuteJsData { r#type: "executeJS".to_string(), source_code: format!("cc.js.getClassByName('{class_name}').getInstance().request('{event_name}', {body}, function(e) {});", class_name = class_name, event_name = event_name, body = body.to_string()) }
    }
}

#[derive(Serialize)]
pub struct DelayEvent {
    data: ExecuteJsData,
    delay: Delay,
}


impl DelayEvent {
    pub fn new(data: ExecuteJsData, delay: Delay) -> DelayEvent {
        DelayEvent { data: data, delay: delay }
    }
}
//pub fn create_delay_event(min_tick_amount: i32, max_tick_amount: i32) {
//    let delay = Delay::new().before_between(min_tick_amount, max_tick_amount).after_between(min_tick_amount, max_tick_amount);
//}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_create_delay_between() {
        let delay = create_delay_between(1, 2);

        assert!(delay > 1, "{} must be more 1", delay);
        assert!(delay < 2000, "{} must be less 2000", delay);
    }
}